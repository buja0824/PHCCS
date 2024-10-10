package PHCCS.service.post.repository;

import PHCCS.service.post.Post;
import PHCCS.service.post.PostUpdateDTO;
import PHCCS.service.post.repository.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
@Repository
@RequiredArgsConstructor

public class MybatisPostRepository implements PostRepository {

    private final PostMapper mapper;

    @Override
    public int save(String category, Post post) {
        return mapper.save(category, post);
    }

    @Override
    public Post showPost(String category, Long postId) {
        log.info("|se|re|showPost()");
        Post post = mapper.showPost(category, postId);
        log.info("re post = {}", post);
        return post;
    }

    @Override
    public List<Post> showAllPost(String category, Long offset, Long size) {
        log.info("|se|re|showAllPost()");

        List<Post> posts = mapper.showAllPost(category, offset, size);
        log.info("repo = {}",posts.toString());
        return posts;
    }

    @Override
    public void updatePost(Long memberId, Long postId, PostUpdateDTO dto, String fileDir) {
        log.info("|se|re|modifyPost()");
        mapper.updatePost(memberId, postId, dto, fileDir);
    }

    @Override
    public void deletePost(String category, Long memberId, Long postId) {
        log.info("|se|re|deletePost()");
        mapper.deletePost(category, memberId, postId);
    }

    @Override
    public String findPostDir(String category, Long postId) {
        log.info("|se|re|findPostDir()");
        return mapper.findPostDir(category, postId);
    }

    @Override
    public void incrementViewCount(String category, Long postId) {
        mapper.incrementViewCount(category, postId);
    }

    @Override
    public Long findAuthorId(String category, Long postId) {
        return mapper.findAuthorId(category, postId);
    }

}
