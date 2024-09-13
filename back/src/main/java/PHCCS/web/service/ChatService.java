package PHCCS.web.service;

import PHCCS.domain.ChatRoom;
import PHCCS.domain.Member;
import PHCCS.domain.Message;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.service.domain.ChatConnectDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    @Data
    static class BindSenderAndRoom{
        private Long entryId;
        private String roomId;

        @Builder
        public BindSenderAndRoom(Long sender, String roomId) {
            this.entryId = sender;
            this.roomId = roomId;
        }
    }

    private static final String SECRET_KEY = "OapJ2D0zLQs4S1FdY5TgRhYKJffpMq7RaNmbN4XURRs";
    private final MemberRepository repository;
    private final ObjectMapper objectMapper;
    private Map<String, ChatRoom> chatRooms; // 채팅방들의 목록
    private Map<BindSenderAndRoom, WebSocketSession> sessions; // 연결된 세션들의 목록

    @PostConstruct //모든 Bean 의존성 주입이 완료되고 실행되어야 하는 메서드에 사용
    private void init() { // 마지막에 초기화가 진행되어진다
        chatRooms = new ConcurrentHashMap<>();
        sessions = new ConcurrentHashMap<>();
    }
    //모든 방을 찾는 메서드
    public List<ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }
    //id로 방을 찾고 결과로 ChatRoom 객체 반환
    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }
    //방 생성 메서드
    public ChatRoom createRoom(ChatConnectDTO chatConnectDTO) {
        Member createMember = repository.findMemberById(chatConnectDTO.getCreateMemberId()).get();
        Member participatingMember = repository.findMemberById(chatConnectDTO.getParticipatingMemberId()).get();
        String roomId = createRoomId(createMember, participatingMember);
        log.info("2. 생성된 방의 ID = {}", roomId);
        // roomId 생성
        ChatRoom chatRoom = ChatRoom.builder() //builder로 변수 세팅
                .roomId(roomId)
                .name(createMember.getName())
                .build();

        chatRooms.put(roomId, chatRoom); //방 생성 후 방 목록에 추가
        /**
         * TODO
         * a 사용자가 방을 생성하였으니 같이 채팅할 b 사용자에게 이 방에 들어오라는 알림을 보내야 함
         * SSE 방식을 이용해서 구현할 예정임
         */

        log.info("##웹 소켓으로 통신 업그레이드 시키면 됨##");
        return chatRoom;
    }

    public void enterRoom(WebSocketSession session){
        log.info("##세션.getLocalAddress() : {}", session.getLocalAddress());
        log.info("##세션.getRemoteAddress() : {}", session.getRemoteAddress());
        log.info("##세션.getHandshakeHeaders() : {}", session.getHandshakeHeaders());
        log.info("##세션.getAcceptedProtocol() : {}", session.getAcceptedProtocol());
        log.info("##세션.getAttributes() : {}", session.getAttributes());
        log.info("##세션.getId() : {}", session.getId());
        log.info("##세션.getUri() : {}", session.getUri());
        log.info("##세션.getQuery() : {}", session.getUri().getQuery());
        String query = session.getUri().getQuery();
        String[] queryList = query.split("&");
        Map<String, String> queryMap = new HashMap<>();
        for (String indexQuery : queryList) {
            String[] split = indexQuery.split("=");
            queryMap.put(split[0], split[1]);
        }
        log.info("queryMap = {}", queryMap);
        String roomId = queryMap.get("roomId");
        String type = queryMap.get("type");

        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();
        List<String> auth = handshakeHeaders.get("authorization");
        String token = auth.get(0).substring(7);
        log.info("token = {}" ,token);
        /**
         * TODO
         * 토큰에서 사용자 정보 추출 -> 사용자의 ID 추출
         * 사용자의 ID와 쿼리 파라미터로 넘어온 roomId를 이용해서 생성된 방이 있는지 찾기
         * 생성된 방이 존재하면 그 방에 메시지를 보낼 수 있음
         */
        Long entryId = extractUserIdFromToken(token);
        ChatRoom findRoom = findRoomById(roomId);
        if(findRoom == null){
            return;
        }
        if(type.equals("enter")){
            BindSenderAndRoom bindSenderAndRoom = BindSenderAndRoom.builder()
                    .sender(entryId)
                    .roomId(roomId)
                    .build();

            sessions.put(bindSenderAndRoom, session);
            Message message = new Message();
            Member entryMember = repository.findMemberById(entryId).get();
            message.setMessage(entryMember.getName()+" 님이 입장하였습니다.");
            try {
                session.sendMessage(new TextMessage(roomId + " 채팅방 입장 성공"));
                sendToMessage(message, roomId);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    public void handlerActions(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        String payload = message.getPayload(); // 메시지 가져오기

        log.info("3. 받은 메시지: {}", payload);

        // payload를 객체로 변환
        Message chatMessage = objectMapper.readValue(payload, Message.class);
        chatMessage.setTimestamp(LocalDateTime.now());

        // 생성된 방 찾기
        String query = session.getUri().getQuery();
        String[] queryList = query.split("&");
        Map<String, String> queryMap = new HashMap<>();
        for (String indexQuery : queryList) {
            String[] split = indexQuery.split("=");
            queryMap.put(split[0], split[1]);
        }
        log.info("queryMap = {}", queryMap);
        String roomId = queryMap.get("roomId");
        String type = queryMap.get("type");

        sendToMessage(chatMessage, roomId);
        //메세지 전송
    }
    private void sendToMessage(Message message, String roomId) {
//        sessions.parallelStream()
//                .forEach(session -> sendMessage(session, message));
        for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) {
            String findRoomId = bindSenderAndRoom.getRoomId();
            if(findRoomId.equals(roomId)){
                WebSocketSession session = sessions.get(bindSenderAndRoom);
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void closeSession(){

    }
    private static String createRoomId(Member createMember, Member participatingMember) {
        return createMember.getEmail() +"-"+ createMember.getId() +"-"+
                participatingMember.getEmail() +"-"+ participatingMember.getId();
    }
    private static Long extractUserIdFromToken(String token) {
        // 토큰에서 Claims 추출
        Claims payload = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Claims에서 id 값 추출
        Long entryId = Long.parseLong(payload.get("id").toString());
        return entryId;
    }
}
