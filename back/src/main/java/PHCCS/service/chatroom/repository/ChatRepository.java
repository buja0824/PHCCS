package PHCCS.service.chatroom.repository;


import PHCCS.service.Message.Message;
import PHCCS.service.chatroom.ChatRoom;

public interface ChatRepository {
    void saveChatRoom(ChatRoom chatRoom);
    void deleteRoom(String roomId);
    void saveChatLog(String roomId, Message message, Long loggerId);
}
