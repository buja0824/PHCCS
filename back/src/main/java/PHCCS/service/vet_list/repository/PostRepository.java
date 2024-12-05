package PHCCS.service.vet_list.repository;

import PHCCS.service.vet_list.Vet;
import PHCCS.service.vet_list.dto.PostUpdateDTO;

import java.util.List;

public interface PostRepository {
    Long save(String category, Vet post);

    void insertDir(String category, Long postId, String dir);

    Vet showPost(String category, Long postId);

    List<PostHeaderDTO> showAllPost(String category, String searchName, Long offset, Long size);

    void updatePost(Long memberId, Long postId, PostUpdateDTO param, String fileDir);

    void deletePost(String category, Long memberId, Long postId);

    String findPostDir(String category, Long postId);

    void incrementViewCount(String category, Long postId);

    Long findAuthorId(String category, Long postId);

    List<MyPostDTO> showMyPost(Long memberId);
    List<LikedPostDTO> showLikedPosts(Long memberId);

    void incrementLike(String category, Long postId);
    void decrementLike(String category, Long postId);
    Boolean isLikeMember(Long memberId,String category, Long postId);
    void likeMember(Long memberId, String category, Long postId);
    void unLikeMember(Long memberId, String category, Long postId);
}
