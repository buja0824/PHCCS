package practice.pra.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Board {

    private Long id;        // 키 속성
    private String title;   // 글 제목
    private String author;  // 작성자
    private String content; // 글 내용
    private LocalDateTime wdate = LocalDateTime.now();

}
