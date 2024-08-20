package practice.pra.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.pra.domain.Board;
import practice.pra.domain.repository.mapper.BoardMapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final BoardMapper mapper;

    public void save(Board board){mapper.save(board);}

    public Optional<Board> readBoard(Long id, String author){
        Optional<Board> board = mapper.readBoard(id, author);
        return board;
    }

    public List<Board> findAllBoard(){
        return mapper.findAllBoard();
    }
}
