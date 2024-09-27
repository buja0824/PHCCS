package PHCCS.web.repository.mapper;

import PHCCS.domain.Post;
import PHCCS.web.repository.domain.PostUpdateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    int save(@Param("category")String category , @Param("post") Post post);

    Post showPost(@Param("category") String category, @Param("id") Long id);
    List<Post> showAllPost(@Param("category") String category);
    void updatePost(@Param("memberId") Long memberId, @Param("postId") Long postId, @Param("dto") PostUpdateDTO dto, @Param("dir") String fileDir);
    void deletePost(@Param("category") String category, @Param("memberId") Long memberId, @Param("postId") Long postId);
    String findPostDir(@Param("category") String category, @Param("id") Long postId);
    void incrementViewCount(@Param("category") String category, @Param("id") Long postId);
    Long findAuthorId(@Param("category") String category, @Param("id") Long postId);
}
