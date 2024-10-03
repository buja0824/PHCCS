package PHCCS.web.service;

import PHCCS.domain.ChatRoom;
import PHCCS.domain.Member;
import PHCCS.domain.Message;
import PHCCS.web.repository.ChatRepository;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.service.domain.ChatConnectDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
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
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    // key : roomId - value : ChatRoom
    private Map<String, ChatRoom> chatRooms; // 채팅방들의 목록
    // key : bindSenderAndRoom - value webSocketSession
    private Map<BindSenderAndRoom, WebSocketSession> sessions; // 연결된 세션들의 목
    /**
     * 채팅 내역을 저장할 맵 키는 챗룸, 벨류는 음..리스트??
     */
    private Map<ChatRoom, Message> chatLog;
    private final ChatRepository chatRepository;

    @PostConstruct //모든 Bean 의존성 주입이 완료되고 실행되어야 하는 메서드에 사용
    private void init() { // 마지막에 초기화가 진행되어진다
        chatRooms = new ConcurrentHashMap<>();
        sessions = new ConcurrentHashMap<>();
        chatLog = new ConcurrentHashMap<>();
    }
    //모든 방을 찾는 메서드
    public List<ChatRoom> findAllRoom(Long memberId) {
        Iterator<ChatRoom> iterator = chatRooms.values().iterator();
        List<ChatRoom> memberJoinRooms = new ArrayList<>();
        while(iterator.hasNext()){
            ChatRoom next = iterator.next();
            if(next.getCreateMemberId().equals(memberId) || next.getParticipatingMemberId().equals(memberId)){
                memberJoinRooms.add(chatRooms.get(next.getRoomId()));
            }
        }
        return memberJoinRooms;
    }
    //id로 방을 찾고 결과로 ChatRoom 객체 반환
    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }
    //방 생성 메서드
    public ChatRoom createRoom(ChatConnectDTO chatConnectDTO) {
        Member createMember = memberRepository.findMemberById(chatConnectDTO.getCreateMemberId()).get();
        Member participatingMember = memberRepository.findMemberById(chatConnectDTO.getParticipatingMemberId()).get();
        String roomId = createRoomId(createMember, participatingMember);
        log.info("2. 생성된 방의 ID = {}", roomId);
        // roomId 생성
        ChatRoom chatRoom = ChatRoom.builder() //builder로 변수 세팅
                .roomId(roomId)
                .createMemberId(createMember.getId())
                .participatingMemberId(participatingMember.getId())
                .build();

        // 메모리에 방을 저장해서 빠른 접근이 가능하게 하기 위함
        chatRooms.put(roomId, chatRoom); //방 생성 후 방 목록에 추가
        // 데이터베이스에 저장하여 방 생성 내역을 저장
        chatRepository.saveChatRoom(chatRoom);

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
        Map<String, String> queryMap = getQueryVal(session);
        String roomId = queryMap.get("roomId");
//        String type = queryMap.get("type");

        Long entryId = getEntryId(session);
        /**
         * 토큰에서 사용자 정보 추출 -> 사용자의 ID 추출
         * 사용자의 ID와 쿼리 파라미터로 넘어온 roomId를 이용해서 생성된 방이 있는지 찾기
         * 생성된 방이 존재하면 그 방에 메시지를 보낼 수 있음
         */
        ChatRoom findRoom = findRoomById(roomId);
        if(findRoom == null){
            // TODO 방이 없으면 에러 발생
            log.error("생성된 채팅방이 존재하지 않습니다.");
            return;
        }
        BindSenderAndRoom bindSenderAndRoom = BindSenderAndRoom.builder()
                .sender(entryId)
                .roomId(roomId)
                .build();

        sessions.put(bindSenderAndRoom, session);
        Message message = new Message();
        Member entryMember = memberRepository.findMemberById(entryId).get();
        message.setMessage(entryMember.getNickName()+" 님이 입장하였습니다.");
        try {
            session.sendMessage(new TextMessage(roomId + " 채팅방 입장 성공"));
            sendToMessage(message, roomId);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void handlerActions(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        String payload = message.getPayload(); // 메시지 가져오기

        log.info("3. 받은 메시지: {}", payload);

        // payload를 객체로 변환
        Message chatMessage = objectMapper.readValue(payload, Message.class);
        chatMessage.setTimestamp(LocalDateTime.now());

        // 생성된 방 찾기
        Map<String, String> queryMap = getQueryVal(session);
        String roomId = queryMap.get("roomId");
//        String type = queryMap.get("type");
        ChatRoom findRoom = findRoomById(roomId);
        if(findRoom == null){
            log.error("생성된 방이 존재하지 않음");
            // TODO 방이 없으면 에러 발생
        }
        /**
        * 음 채팅방을 나갔다가 (소켓 닫기가 아닌 그냥 뒤로가기) 다시 들어가면 그 채팅방의 메시지 내역을 다시 로드 해줘야 하는데
        * 그걸 어떻게 구현?
        * 처음 방에 접속하여 나눈 채팅들을 모두 저장하여 주고 채팅방을 나갔다가 들어오면 그 저장된 채팅내역들 출력하면 될거 같다         *
        */
        if(chatMessage.getType().equals(Message.MessageType.ENTER)){ 
            // 방을 나갔다가 다시 들어오는거면 (소켓닫기가 아닌)
            // 이전에 저장된 채팅 내역들을 뿌려주기
            Message chatLogs = chatLog.get(findRoom);
            sendToMessage(chatLogs, roomId);
        }
        //메세지 전송
        sendToMessage(chatMessage, roomId);
        //메시지 내역 저장
        chatLog.put(findRoom, chatMessage);
        log.info(chatLog.toString());
    }
    private void sendToMessage(Message message, String roomId) {
//        ChatRoom chatRoom = findRoomById(roomId);
        // 연결된 세션들에서 해당 채팅방의 세션이 존재 하는지 확인하고 그 세션 전부에게 메시지 전송
        for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) {
            String findRoomId = bindSenderAndRoom.getRoomId(); /// 반복문을 돌았으니 2개의 세션이 나올것
            if(findRoomId.equals(roomId)){
                WebSocketSession session = sessions.get(bindSenderAndRoom);
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//                    chatLog.put(chatRoom, message);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param session
     * @param status
     * 1개 채팅방 2개의 세션, 1 채팅방의 2개의 세션이 모두 닫히면 그 채팅방도 삭제 시키기
     * 1 채팅방의 1 세션이 닫히면 그 채팅방에 있는 다른 세션에게 사용자가 채팅방 나갔다고 알리기
     */
    public void closeSession(WebSocketSession session, CloseStatus status){
        log.info("### 상태코드 = {}", status.getCode());
        log.info("### 메 시 지 = {}", status.getReason());
        Map<String, String> queryMap = getQueryVal(session);
        String roomId = queryMap.get("roomId");
        Long entryId = getEntryId(session);
        Member entryMember = memberRepository.findMemberById(entryId).get();
        int cnt = 0;
        for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) {
            String findRoomId = bindSenderAndRoom.getRoomId();
            if(findRoomId.equals(roomId))
                cnt++;
        }
        switch (cnt){
            case 2:
                for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) {
                    if(bindSenderAndRoom.getRoomId().equals(roomId) && bindSenderAndRoom.getEntryId().equals(entryId)){
                        sessions.remove(bindSenderAndRoom);
                        Message message = new Message();
                        message.setMessage(entryMember.getNickName() + " 님이 채팅방을 떠났습니다.");
                        sendToMessage(message, roomId);
                    }
                }
                break;
            case 1:
                for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) {
                    if(bindSenderAndRoom.getRoomId().equals(roomId) && bindSenderAndRoom.getEntryId().equals(entryId)){
                        sessions.remove(bindSenderAndRoom);
                        chatRooms.remove(roomId);
                        chatRepository.deleteRoom(roomId);
                    }
                }
                break;
        }
    }

    private static String createRoomId(Member createMember, Member participatingMember) {
        return createMember.getEmail() +"-"+ createMember.getId() +"-"+
                participatingMember.getEmail() +"-"+ participatingMember.getId();
    }

    private static Map<String, String> getQueryVal(WebSocketSession session) {
        String query = session.getUri().getQuery();
        String[] queryList = query.split("&");
        Map<String, String> queryMap = new HashMap<>();
        for (String indexQuery : queryList) {
            String[] split = indexQuery.split("=");
            queryMap.put(split[0], split[1]);
        }
        log.info("queryMap = {}", queryMap);
        return queryMap;
    }

    private static Long getEntryId(WebSocketSession session) {
        String token = getAuthToken(session);
        Long entryId = extractUserIdFromToken(token);
        return entryId;
    }

    private static String getAuthToken(WebSocketSession session) {
        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();
        List<String> auth = handshakeHeaders.get("authorization");
        String token = auth.get(0).substring(7);
        log.info("token = {}" ,token);
        return token;
    }

    /**
     * 멤버의 토큰을 가져오는 기능이 아직 머지 안되어서 임시용 으로 만든 메서드
     * */
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
