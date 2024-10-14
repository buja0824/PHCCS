package PHCCS.service.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id; // 댓글의 pk
    private Long postId; // 댓글이 달린 게시글의 pk 외래키임
    private Long memberId; // 댓글 작성자의 pk 외래키로 사용
    private String comment; // 댓글의 내용
    private String nickName; // 댓글작성자
    private Long likeCnt;
    private LocalDateTime writeTime = LocalDateTime.now(); // 댓글 작성 시간
}
