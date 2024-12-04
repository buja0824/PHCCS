package PHCCS.service.chatroom.repository;


import PHCCS.service.chatroom.dto.Message;
import PHCCS.service.chatroom.ChatRoom;

import java.util.List;

public interface ChatRepository {
    void saveChatRoom(ChatRoom chatRoom);
    void deleteRoom(String roomId);
    void saveChatLog(String roomName, String roomId, Message message, Long loggerId);

    List<Message> getChatLog(Long memberId, String roomId);
}
