package PHCCS.web.service.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id; // 게시판의 pk

    private String category; // 게시판 종류
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String author; // 게시글 작성자 : 회원의 닉네임을 받아와야 함
    private LocalDateTime writeTime = LocalDateTime.now(); // 게시글 작성날짜와 시간
}
