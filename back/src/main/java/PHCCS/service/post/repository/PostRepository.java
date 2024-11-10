package PHCCS.service.post.repository;

import PHCCS.service.post.dto.LikedPostDTO;
import PHCCS.service.post.dto.MyPostDTO;
import PHCCS.service.post.Post;
import PHCCS.service.post.dto.PostHeaderDTO;
import PHCCS.service.post.dto.PostUpdateDTO;

import java.util.List;

public interface PostRepository {
    Long save(String category, Post post);

    void insertDir(String category, Long postId, String dir);

    Post showPost(String category, Long postId);

    List<PostHeaderDTO> showAllPost(String category, String searchName, Long offset, Long size);

    void updatePost(Long memberId, Long postId, PostUpdateDTO param, String fileDir);

    void deletePost(String category, Long memberId, Long postId);

    String findPostDir(String category, Long postId);

    void incrementViewCount(String category, Long postId);

    Long findAuthorId(String category, Long postId);

    List<MyPostDTO> showMyPost(Long memberId);
    List<LikedPostDTO> showLikedPosts(Long memberId);

    void incrementLike(String category, Long postId);
    Boolean isLikeMember(Long memberId,String category, Long postId);
    void likeMember(Long memberId, String category, Long postId);
}
