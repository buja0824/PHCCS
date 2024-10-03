package PHCCS.web.repository;


import PHCCS.domain.ChatRoom;

public interface ChatRepository {
    void saveChatRoom(ChatRoom chatRoom);
    void deleteRoom(String roomId);
}
