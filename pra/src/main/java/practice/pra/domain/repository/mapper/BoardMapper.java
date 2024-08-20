package practice.pra.domain.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practice.pra.domain.Board;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    void save(Board board);

    Optional<Board> readBoard(@Param("id") Long id, @Param("author") String author);

    List<Board> findAllBoard();
}
