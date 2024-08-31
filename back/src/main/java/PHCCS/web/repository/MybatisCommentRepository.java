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
}
