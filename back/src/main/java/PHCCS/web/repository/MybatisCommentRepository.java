package PHCCS.web.repository;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;
import PHCCS.web.repository.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisCommentRepository implements CommentRepository{

    private final CommentMapper mapper;

    @Override
    public int save(String category, Comment comment) {
        return mapper.save(category, comment);
    }

    @Override
    public List<Comment> findAllComment(String category, Long postId) {
        return mapper.findAllComment(category, postId);
    }

    @Override
    public void updateComment(CommentDto dto) {
        mapper.updateComment(dto);
    }

    @Override
    public void deleteComment(String category, Long postId, Long commentId) {
        mapper.deleteComment(category, postId, commentId);
    }

    @Override
    public void incrementLike(String category, Long postId, Long commentId) {
        mapper.incrementLike(category, postId, commentId);
    }

    @Override
    public boolean isLikeMember(Long memberId, String category, Long postId, Long commentId) {
        return mapper.isLikeMember(memberId, category, postId, commentId);
    }
}
