package PHCCS.domain;

import PHCCS.web.service.ChatService;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChatRoom {
    private String roomId;
    private Long createMemberId;
    private Long participatingMemberId;
//    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId, Long createMemberId, Long participatingMemberId) {
        this.roomId = roomId;
        this.createMemberId = createMemberId;
        this.participatingMemberId = participatingMemberId;
    }
//    public void handlerActions(WebSocketSession session, Message chatMessage, ChatService chatService) {
//        if (chatMessage.getType().equals(Message.MessageType.ENTER)) {
//            //방에 처음 들어왔을때
//            sessions.add(session);
//            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
//        }
//
//        sendMessage(chatMessage, chatService);
//        //메세지 전송
//    }
//
//    private <T> void sendMessage(T message, ChatService chatService) {
//        sessions.parallelStream()
//                .forEach(session -> chatService.sendMessage(session, message));
//        //채팅방에 입장해 있는 모든 클라이언트에게 메세지 전송
//    }

}
