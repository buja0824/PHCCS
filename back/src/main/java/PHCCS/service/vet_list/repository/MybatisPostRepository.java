package PHCCS.service.vet_list.repository;

import PHCCS.service.vet_list.Vet;
import PHCCS.service.vet_list.dto.PostUpdateDTO;
import PHCCS.service.vet_list.repository.mapper.PostMapper;
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
    public Long save(String category, Vet post) {
        return mapper.save(category, post);
    }

    @Override
    public void insertDir(String category, Long postId, String dir) {
        mapper.insertDir(category, postId, dir);
    }

    @Override
    public Vet showPost(String category, Long postId) {
        log.info("|se|re|showPost()");
        Vet post = mapper.showPost(category, postId);
        log.info("re post = {}", post);
        return post;
    }

    @Override
    public List<PostHeaderDTO> showAllPost(String category, String searchName, Long offset, Long size) {
        log.info("|se|re|showAllPost()");
        List<PostHeaderDTO> posts = mapper.showAllPost(category, searchName, offset, size);
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

    @Override
    public List<MyPostDTO> showMyPost(Long memberId) {
        return mapper.showMyPost(memberId);
    }

    @Override
    public List<LikedPostDTO> showLikedPosts(Long memberId) {
        return mapper.showLikedPosts(memberId);
    }

    @Override
    public Boolean isLikeMember(Long memberId, String category, Long postId) {
        return mapper.isLikeMember(memberId, category, postId);
    }

    @Override
    public void incrementLike(String category, Long postId) {
        mapper.incrementLike(category, postId);
    }

    @Override
    public void decrementLike(String category, Long postId) {
        mapper.decrementLike(category, postId);
    }

    @Override
    public void likeMember(Long memberId, String category, Long postId) {
        mapper.likeMember(memberId, category, postId);
    }

    @Override
    public void unLikeMember(Long memberId, String category, Long postId) {
        mapper.unLikeMember(memberId, category, postId);
    }

}
