package PHCCS.web.repository;

import PHCCS.domain.Post;
import PHCCS.web.repository.domain.PostModifyParam;
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
    public int communitySave(Post post) {
        return mapper.communitySave(post);
    }

    @Override
    public int qnaSave(Post post) {
        return mapper.qnaSave(post);
    }

    @Override
    public int vetSave(Post post) {
        return mapper.vetSave(post);
    }

    @Override
    public Post showPost(String category, Long postId) {
        log.info("|se|re|showPost()");
        Post post = mapper.showPost(category, postId);
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

    @Override
    public void modifyPost(Long memberId, Long postId, PostModifyParam param, String fileDir) {
        log.info("|se|re|modifyPost()");
        mapper.modifyPost(memberId, postId, param, fileDir);
    }

    @Override
    public void deletePost(String category, Long memberId, Long postId) {
        log.info("|re|deletePost()");
        mapper.deletePost(category, memberId, postId);
    }

    @Override
    public String findPostDir(String category, Long postId) {
        log.info("|se|re|findPostDir()");
        return mapper.findPostDir(category, postId);
    }

}
