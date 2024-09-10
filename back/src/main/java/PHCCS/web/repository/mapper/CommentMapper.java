package PHCCS.web.repository.mapper;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int save(@Param("category") String category, @Param("comment") Comment comment);
    List<Comment> findAllComment(@Param("category") String category, @Param("postId") Long postId);
    void updateComment(@Param("dto") CommentDTO dto);
    void deleteComment(@Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    void incrementLike(@Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
    Boolean isLikeMember(@Param("memberId")Long memberId, @Param("category") String category, @Param("postId") Long postId, @Param("commentId") Long commentId);
}
