package PHCCS.web.repository;

import PHCCS.domain.Post;
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
