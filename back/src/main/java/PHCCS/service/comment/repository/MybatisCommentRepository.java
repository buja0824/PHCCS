package PHCCS.service.comment.repository;

import PHCCS.service.comment.Comment;
import PHCCS.service.comment.dto.CommentAddDTO;
import PHCCS.service.comment.dto.CommentDTO;
import PHCCS.service.comment.repository.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisCommentRepository implements CommentRepository {

    private final CommentMapper mapper;

    @Override
    public int save(Long loginMember, String category, Long postId, CommentAddDTO comment) {
        return mapper.save(loginMember,category, postId, comment);
    }

    @Override
    public List<Comment> findAllComment(String category, Long postId) {
        return mapper.findAllComment(category, postId);
    }

    @Override
    public void updateComment(CommentDTO dto) {
        mapper.updateComment(dto);
    }

    @Override
    public void deleteComment(String category, Long postId, Long commentId) {
        mapper.deleteComment(category, postId, commentId);
    }
    @Override
    public Boolean isLikeMember(Long memberId, String category, Long postId, Long commentId) {
        Boolean likeMember = mapper.isLikeMember(memberId, category, postId, commentId);
        log.info("likeMember : {}", likeMember);
        return likeMember;
    }

    @Override
    public void incrementLike(String category, Long postId, Long commentId) {
        mapper.incrementLike(category, postId, commentId);
    }

    @Override
    public void likeMember(Long memberId, String category, Long postId, Long commentId) {
        mapper.likeMember(memberId, category, postId, commentId);
    }
}
