package PHCCS.web.controller;

import PHCCS.service.post.PostService;
import PHCCS.service.post.PostDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
class BoardControllerTest {


    @Autowired private PostService service;

    @Test
    void addPostTest() throws IOException {
        PostDTO postDto = new PostDTO();
        postDto.setAuthor("작성자");
        postDto.setCategory("qna_board");
        postDto.setContent("테스트코드에서 작성");
        postDto.setTitle("테스트코드에서 작성한 제목");
        postDto.setWriteTime(LocalDateTime.now());
        service.save(6L, postDto, null, null);
    }

    @Test
    void showPostTest(){
        ResponseEntity<?> responseEntity = service.showPost("qna_board", 6L);
        log.info(responseEntity.toString());
    }
}