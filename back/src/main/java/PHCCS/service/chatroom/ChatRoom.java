package PHCCS.service.chatroom;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatRoom {
    private String roomId;
    private Long createMemberId;
    private Long invitedMemberId;
//    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId, Long createMemberId, Long participatingMemberId) {
        this.roomId = roomId;
        this.createMemberId = createMemberId;
        this.invitedMemberId = participatingMemberId;
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
