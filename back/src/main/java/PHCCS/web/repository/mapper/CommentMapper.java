package PHCCS.web.repository.mapper;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int save(@Param("category") String category, @Param("comment") Comment comment);
    List<Comment> findAllComment(@Param("category") String category, @Param("id") Long postId);
    void updateComment(@Param("dto") CommentDto dto);
}
