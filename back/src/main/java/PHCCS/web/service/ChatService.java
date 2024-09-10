package PHCCS.web.service;

import PHCCS.domain.ChatRoom;
import PHCCS.domain.Member;
import PHCCS.domain.Message;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.service.domain.ChatConnectDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.Session;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

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
    public void handlerActions(WebSocketSession session, Message chatMessage) {
        //방에 처음 들어왔을때
        if (chatMessage.getType().equals(Message.MessageType.ENTER)) {
            // 누구의 세션인지 확인하기 위해 유저의 이름, 채팅방 추출
            String sender = chatMessage.getSender();
            String roomId = chatMessage.getRoomId();

            BindSenderAndRoom bindSenderAndRoom = BindSenderAndRoom.builder()
                    .sender(sender)
                    .roomId(roomId)
                    .build();

            sessions.put(bindSenderAndRoom, session);
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
        }

        sendToMessage(chatMessage);
        //메세지 전송
    }
    private void sendToMessage(Message message) {
//        sessions.parallelStream()
//                .forEach(session -> sendMessage(session, message));
        for (BindSenderAndRoom bindSenderAndRoom : sessions.keySet()) {
            String roomId = bindSenderAndRoom.getRoomId();
            if(roomId.equals(message.getRoomId())){
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
}

@Data
class BindSenderAndRoom{
    private String sender;
    private String roomId;

    @Builder
    public BindSenderAndRoom(String sender, String roomId) {
        this.sender = sender;
        this.roomId = roomId;
    }
}
