package PHCCS.web.repository;

import PHCCS.domain.Post;
import PHCCS.web.repository.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisPostRepository implements PostRepository{

    private final PostMapper mapper;

    @Override
    public int communitySave(Long id, Post post) {
        return mapper.communitySave(id, post);
    }

    @Override
    public int qnaSave(Long id, Post post) {
        return mapper.qnaSave(id, post);
    }

    @Override
    public int vetSave(Long id, Post post) {
        return mapper.vetSave(id, post);
    }

}
