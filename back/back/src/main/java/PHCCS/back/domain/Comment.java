package PHCCS.back.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id; // 댓글의 pk
    private Long posteId; // 댓글이 달린 게시글의 pk 외래키임
    private String content; // 댓글의 내용
    private String author; // 댓글작성자
    private LocalDateTime writeTime = LocalDateTime.now(); // 댓글 작성일
    // 댓글 수정시간은 dto 만들어서 거기서 관리 해야 할 듯
    private int likeCount; // 댓글 좋아요 수
}
