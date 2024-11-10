package PHCCS.service.comment.repository.mapper;

import PHCCS.service.comment.Comment;
import PHCCS.service.comment.dto.CommentAddDTO;
import PHCCS.service.comment.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int save(@Param("memberId") Long memberId, @Param("category") String category, @Param("postId")Long postId, @Param("comment") CommentAddDTO comment);
    List<Comment> findAllComment(@Param("category") String category, @Param("postId") Long postId);
    void updateComment(@Param("dto") CommentDTO dto);
    void deleteComment(@Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    void incrementLike(@Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    Boolean isLikeMember(@Param("memberId")Long memberId, @Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    void likeMember(@Param("memberId")Long memberId, @Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);

}
