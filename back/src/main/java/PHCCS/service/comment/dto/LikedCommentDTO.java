package PHCCS.service.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikedCommentDTO {
    private Long id;
    private Long writerId;
    private String comment;
    private Long postId;
    private Long likeCnt;
    private LocalDateTime writeTime;
    private String category;
}
