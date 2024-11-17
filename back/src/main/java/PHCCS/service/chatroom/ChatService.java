package PHCCS.service.chatroom;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.sse.SSEService;
import PHCCS.service.chatroom.dto.ChatConnectDTO;
import PHCCS.service.member.Member;
import PHCCS.service.Message.Message;
import PHCCS.service.chatroom.repository.ChatRepository;
import PHCCS.service.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    // key : roomId - value : ChatRoom
    private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>(); // 채팅방들의 목록
    // key : bindSenderAndRoom - value webSocketSession
    private Map<BindSenderAndRoom, WebSocketSession> sessions= new ConcurrentHashMap<>(); // 연결된 세션들의 목

    private Map<ChatRoom, Message> chatLog= new ConcurrentHashMap<>();
    private final ChatRepository chatRepository;
    private final JwtUtil jwtUtil;

    // 사용자가 참여중인 모든 방을 찾는 메서드
    public List<ChatRoom> findAllRoom(Long memberId) {
        Iterator<ChatRoom> iterator = chatRooms.values().iterator();
        List<ChatRoom> memberJoinRooms = new ArrayList<>();
        while(iterator.hasNext()){
            ChatRoom next = iterator.next();
            if(next.getCreateMemberId().equals(memberId) || next.getInvitedMemberId().equals(memberId)){
                memberJoinRooms.add(chatRooms.get(next.getRoomId()));
            }
        }
        return memberJoinRooms;
    }

    //방 생성 메서드
    public ChatRoom createRoom(ChatConnectDTO chatConnectDTO) {
        Member createMember = memberRepository.findMemberById(chatConnectDTO.getCreateMemberId()).get();
        Member participatingMember = memberRepository.findMemberById(chatConnectDTO.getParticipatingMemberId()).get();
        String roomName = chatConnectDTO.getRoomName();
        String roomId = createRoomId(createMember, participatingMember);

        log.info("2. 생성된 방의 ID = {}, 이름 = {}", roomId, roomName);
        // roomId 생성
        ChatRoom chatRoom = ChatRoom.builder() //builder로 변수 세팅
                .roomId(roomId)
                .roomName(roomName)
                .createMemberId(createMember.getId())
                .participatingMemberId(participatingMember.getId())
                .build();

        // 메모리에 방을 저장해서 빠른 접근이 가능하게 하기 위함
        chatRooms.put(roomId, chatRoom); //방 생성 후 방 목록에 추가
        // 데이터베이스에 저장하여 방 생성 내역을 저장
//        chatRepository.saveChatRoom(chatRoom);
        log.info("##웹 소켓으로 통신 업그레이드 시키면 됨##");
        return chatRoom;
    }

    public void enterRoom(WebSocketSession session){
        Map<String, String> queryMap = getQueryVal(session);
        String roomId = queryMap.get("roomId");
//        Long participatingMember = Long.parseLong(queryMap.get("another")); // 상대방의 PK 받아오기
        Long entryId = getEntryId(session);
//        ChatConnectDTO chatConnectDTO = new ChatConnectDTO();
//        chatConnectDTO.setCreateMemberId(entryId);
//        chatConnectDTO.setParticipatingMemberId(participatingMember);
//        ChatRoom room = createRoom(chatConnectDTO);
//        String roomId = room.getRoomId();
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
        BindSenderAndRoom bindSenderAndRoom =
                BindSenderAndRoom.builder()
                    .sender(entryId)
                    .roomId(roomId)
                    .build();
        chatRepository.getChatLog(entryId, roomId);
        int memberCnt = 0;
        for (BindSenderAndRoom senderAndRoom : sessions.keySet()) {
            String findRoomId = senderAndRoom.roomId;
            if(findRoomId.equals(roomId))
                memberCnt++; // 멤버 발견하면 인원수 증가
        }
//        Message message = new Message();
//        Member entryMember;
        switch (memberCnt){
            case 0:
                enterRoomAndSendAlarm(session, bindSenderAndRoom, entryId, findRoom, roomId);
//                Member entryMember;
                break;

            case 1:
                enterRoomAndSendAlarm(session, bindSenderAndRoom, entryId, findRoom, roomId);
                break;
            default:
                throw new BadRequestEx("ID : " + roomId+ " 채팅방 최대 인원수 초과");
        }

    }

    private void enterRoomAndSendAlarm(WebSocketSession session, BindSenderAndRoom bindSenderAndRoom, Long entryId, ChatRoom findRoom, String roomId) {
        sessions.put(bindSenderAndRoom, session);
        Message message = new Message();
        Member entryMember = memberRepository.findMemberById(entryId).get();
        message.setMessage(entryMember.getNickName()+" 님이 입장하였습니다.");
        try {
            session.sendMessage(new TextMessage(findRoom.getRoomName() + " 채팅방 입장 성공"));
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
        * 처음 방에 접속하여 나눈 채팅들을 모두 저장하여 주고 채팅방을 나갔다가 들어오면 그 저장된 채팅내역들 출력하면 될거 같다
         * 24.10.03
         * 그냥 뒤로 가기이면 채팅 내역이 이미 그대로 남아 있으니 다시 뿌려줄 필요가 없음
         * 소켓 닫기일 경우에 다시 뿌려주어야 하는데 프론트랑 이야기 해봐야 할 듯
         *
        */
//        if(chatMessage.getType().equals(Message.MessageType.ENTER)){
//            // 방을 나갔다가 다시 들어오는거면 (소켓닫기)
//            // 이전에 저장된 채팅 내역들을 뿌려주기
//            Message chatLogs = chatLog.get(findRoom);
//            sendToMessage(chatLogs, roomId);
//        }
        Long loggerId = getEntryId(session);
        chatRepository.saveChatLog(roomId, chatMessage,loggerId);
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
        int memberCnt = 0;
        for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) { // 한개 채팅방에 멤버가 몇명 있는지 찾기 위함
            String findRoomId = bindSenderAndRoom.getRoomId();
            if(findRoomId.equals(roomId))
                memberCnt++; // 멤버 발견하면 인원수 증가
        }
        switch (memberCnt){
            case 2: // 인원이 2명이면
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

    //id로 방을 찾고 결과로 ChatRoom 객체 반환
    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    // 채팅방 기본키 생성 어떤식으로 해야할지
    private static String createRoomId(Member createMember, Member participatingMember) {
        return createMember.getId() +"-"+
                participatingMember.getId();
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

    private Long getEntryId(WebSocketSession session) {
        return getAuthToken(session);
    }

    private Long getAuthToken(WebSocketSession session) {
        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();
        List<String> auth = handshakeHeaders.get("authorization");
        log.info("auth : {}", auth);
        Long entryId = jwtUtil.extractSubject(auth.get(0));

//        String token = auth.get(0).substring(7);
        log.info("entryMemberId = {}" ,entryId);
        return entryId;
    }
}
