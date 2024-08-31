package PHCCS.web.repository;

import PHCCS.domain.Post;
import PHCCS.web.repository.domain.PostUpdateDto;

import java.util.List;

public interface PostRepository {
    int save(String category, Post post);

    Post showPost(String category, Long postId);
    List<Post> showAllPost(String category);
    void updatePost(Long memberId, Long postId, PostUpdateDto param, String fileDir);
    void deletePost(String category, Long memberId, Long postId);
    String findPostDir(String category, Long postId);
    void incrementViewCount(String category, Long postId);
}
