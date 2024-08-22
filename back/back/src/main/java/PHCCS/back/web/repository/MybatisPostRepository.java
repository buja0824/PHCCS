package PHCCS.back.web.repository;

import PHCCS.back.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisPostRepository implements PostRepository{


    @Override
    public int save(Post post) {
        return 0;
    }
}
