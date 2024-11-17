package PHCCS.service.chatroom.repository;

import PHCCS.service.Message.Message;
import PHCCS.service.chatroom.ChatRoom;
import PHCCS.service.chatroom.repository.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisChatRepository implements ChatRepository {
    private final ChatMapper mapper;
    @Override
    public void saveChatRoom(ChatRoom chatRoom) {
        log.info("|se|re|chatRoom = {}", chatRoom);
        mapper.saveChatRoom(chatRoom);
    }

    @Override
    public void deleteRoom(String roomId) {
        mapper.deleteRoom(roomId);
    }

    @Override
    public void saveChatLog(String roomId, Message message, Long loggerId) {
        mapper.saveChatLog(roomId, message, loggerId);
    }

    @Override
    public List<Message> getChatLog(Long memberId, String roomId) {
        return mapper.getChatLog(memberId, roomId);
    }
}
