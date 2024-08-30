package PHCCS.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id; // 댓글의 pk
    private Long postId; // 댓글이 달린 게시글의 pk 외래키임
    private String content; // 댓글의 내용
    private String author; // 댓글작성자
    private LocalDateTime writeTime = LocalDateTime.now(); // 댓글 작성 시간
}
