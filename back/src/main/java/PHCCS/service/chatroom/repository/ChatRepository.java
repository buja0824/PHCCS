package PHCCS.service.chatroom.repository;


import PHCCS.service.chatroom.ChatRoom;

public interface ChatRepository {
    void saveChatRoom(ChatRoom chatRoom);
    void deleteRoom(String roomId);
}
