package PHCCS.web.controller;

import PHCCS.web.service.PostService;
import PHCCS.web.service.domain.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class BoardControllerTest {


    @Autowired private PostService service;

    @Test
    void addPostTest() throws IOException {
        PostDto postDto = new PostDto();
        postDto.setAuthor("작성자");
        postDto.setCategory("qna_board");
        postDto.setContent("테스트코드에서 작성");
        postDto.setTitle("테스트코드에서 작성한 제목");
        postDto.setWriteTime(LocalDateTime.now());
        service.save(3L, postDto, null, null);
    }
}