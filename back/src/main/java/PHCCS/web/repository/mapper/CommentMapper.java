package PHCCS.web.repository.mapper;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int save(@Param("category") String category, @Param("comment") Comment comment);
    List<Comment> findAllComment(@Param("category") String category, @Param("postId") Long postId);
    void updateComment(@Param("dto") CommentDto dto);
    void deleteComment(@Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    void incrementLike(@Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    boolean isLikeMember(@Param("memberId")Long memberId, @Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
}
