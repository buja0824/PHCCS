package PHCCS.web.repository;

import PHCCS.domain.Post;
import PHCCS.web.repository.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
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

    @Override
    public Post showPost(String category, Long id) {
        log.info("|se|re|showPost()");
        Post post = mapper.showPost(category, id);
        log.info("re post = {}", post);
        return post;
    }

    @Override
    public List<Post> showAllPost(String category) {
        log.info("|se|re|showAllPost()");
        log.info("category = {}", category);

        List<Post> posts = mapper.showAllPost(category);
        log.info("repo = {}",posts.toString());
        return posts;
    }

}
