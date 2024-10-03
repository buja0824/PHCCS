package PHCCS.web.repository;

import PHCCS.domain.ChatRoom;
import PHCCS.web.repository.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
}
