package PHCCS.web.repository;


import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;

import java.util.List;

public interface CommentRepository {

    int save(String category, Comment comment);
    List<Comment> findAllComment(String category, Long postId);
    void updateComment(CommentDto dto);
    void deleteComment(String category, Long postId, Long commentId);
    void incrementLike(String category, Long postId, Long commentId);
    boolean isLikeMember(Long memberId, String category, Long postId, Long commentId);
}
