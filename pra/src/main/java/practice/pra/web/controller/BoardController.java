package practice.pra.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.pra.domain.Board;
import practice.pra.domain.repository.BoardRepository;
import practice.pra.domain.repository.mapper.BoardMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository repository;

    @GetMapping("") // 게시글 목록들을 불러오기
    public List<Board> showBoards(){
        List<Board> allBoard = repository.findAllBoard();
        log.info(allBoard.toString());
        return allBoard;
    }

    @PostMapping("/write") // 게시판 작성 // 파일 업로드 기능은 추후에
    public String writeBoard(@RequestBody Board board){
        log.info("id = {}", board.getId());
        log.info("title = {}", board.getTitle());
        log.info("author = {}", board.getAuthor());
        log.info("content = {}", board.getContent());
        log.info("wdate = {}", board.getWdate());

        repository.save(board);


        return "게시글 저장 완료"; //실제 코딩들어가면 실패, 성공 여부 나눠서 성공하면 200- ok 실패하면 그에 맞는 상태 코드 날리기
    }

    @GetMapping("/read/{id}/{author}") // 게시글 읽어들이기 로그인 여부는 판단하지 않음
    public Board readBoard(@PathVariable("id") Long id,
                          @PathVariable("author") String author){
        // 어떤 게시글인지 특정할 수 있어야 한다. 무엇으로? 게시글id, 작성자의 id
        // 클라이언트에서 게시글의 id랑 작성자의 id를 보내 주어야 하는데 그럼 게시글의 목록을 보는 곳에서 서버측에서 게시글의 id랑 작성자의 id가 담긴 json데이터를 넘겨 주어야 하는가?

        Optional<Board> board = repository.readBoard(id, author);
        return board.get();
    }


}
