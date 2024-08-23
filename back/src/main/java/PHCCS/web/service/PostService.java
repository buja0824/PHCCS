package PHCCS.web.service;

import PHCCS.domain.Post;
import PHCCS.web.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;

    public ResponseEntity<?> save(Long id, Post post){
        int resultRow = repository.save(id, post);
        if(resultRow <= 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
        }
        return ResponseEntity.ok("게시글을 등록 하였습니다.");
    }

}
