package PHCCS.web.repository;

import PHCCS.web.repository.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisChatRepository implements ChatRepository {
    private ChatMapper mapper;

    @Override
    public void save(Long roomId, Long memberId, String chat) {
        mapper.save(roomId, memberId, chat);
    }
}
