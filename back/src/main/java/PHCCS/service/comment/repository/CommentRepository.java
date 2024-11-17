package PHCCS.service.comment.repository;


import PHCCS.service.comment.Comment;
import PHCCS.service.comment.dto.CommentAddDTO;
import PHCCS.service.comment.dto.CommentDTO;
import PHCCS.service.comment.dto.LikedCommentDTO;
import PHCCS.service.comment.dto.MyCommentDTO;

import java.util.List;

public interface CommentRepository {

    int save(Long loginMember, String category, Long postId, CommentAddDTO comment);

    List<Comment> findAllComment(String category, Long postId);

    void updateComment(CommentDTO dto);

    void deleteComment(String category, Long postId, Long commentId);

    List<MyCommentDTO> showMyComments(Long memberId);
    List<LikedCommentDTO> showLikedComments(Long memberId);

    void incrementLike(String category, Long postId, Long commentId);
    void decrementLike(String category, Long postId, Long commentId);

    Boolean isLikeMember(Long memberId, String category, Long postId, Long commentId);

    void likeMember(Long memberId, String category, Long postId, Long commentId);
    void unLikeMember(Long memberId, String category, Long postId, Long commentId);
}
