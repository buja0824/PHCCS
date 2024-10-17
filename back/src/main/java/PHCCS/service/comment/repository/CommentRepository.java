package PHCCS.service.comment.repository;


import PHCCS.service.comment.Comment;
import PHCCS.service.comment.dto.CommentDTO;

import java.util.List;

public interface CommentRepository {

    int save(String category, Comment comment);

    List<Comment> findAllComment(String category, Long postId);

    void updateComment(CommentDTO dto);

    void deleteComment(String category, Long postId, Long commentId);

    void incrementLike(String category, Long postId, Long commentId);
    Boolean isLikeMember(Long memberId, String category, Long postId, Long commentId);
    void likeMember(Long memberId, String category, Long postId, Long commentId);
}
