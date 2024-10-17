package PHCCS.service.post.repository.mapper;

import PHCCS.service.post.dto.MyPostDTO;
import PHCCS.service.post.Post;
import PHCCS.service.post.dto.PostHeaderDTO;
import PHCCS.service.post.dto.PostUpdateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    int save(@Param("category")String category , @Param("post") Post post);

    Post showPost(@Param("category") String category, @Param("id") Long id);
    List<PostHeaderDTO> showAllPost(@Param("category") String category, @Param("searchName") String searchName, @Param("offset") Long offset, @Param("size") Long size);
    void updatePost(@Param("memberId") Long memberId, @Param("postId") Long postId, @Param("dto") PostUpdateDTO dto, @Param("dir") String fileDir);
    void deletePost(@Param("category") String category, @Param("memberId") Long memberId, @Param("postId") Long postId);
    String findPostDir(@Param("category") String category, @Param("id") Long postId);
    void incrementViewCount(@Param("category") String category, @Param("id") Long postId);
    Long findAuthorId(@Param("category") String category, @Param("id") Long postId);
    List<MyPostDTO> showMyPost(Long memberId);
    void incrementLike(@Param("category") String category, @Param("postId") Long postId);
    Boolean isLikeMember(@Param("memberId") Long memberId, @Param("category") String category, @Param("postId") Long postId);
    void likeMember(@Param("memberId") Long memberId, @Param("category") String category, @Param("postId") Long postId);
}
