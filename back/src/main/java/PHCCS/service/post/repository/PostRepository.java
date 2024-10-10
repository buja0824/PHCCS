package PHCCS.service.post.repository;

import PHCCS.service.post.Post;
import PHCCS.service.post.PostUpdateDTO;

import java.util.List;

public interface PostRepository {
    int save(String category, Post post);

    Post showPost(String category, Long postId);
    List<Post> showAllPost(String category, Long offset, Long size);
    void updatePost(Long memberId, Long postId, PostUpdateDTO param, String fileDir);
    void deletePost(String category, Long memberId, Long postId);
    String findPostDir(String category, Long postId);
    void incrementViewCount(String category, Long postId);
    Long findAuthorId(String category, Long postId);
}
