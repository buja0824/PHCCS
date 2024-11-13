package PHCCS.service.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyCommentDTO {
    private Long id;
    private String comment;
    private Long postId;
    private Long likeCnt;
    private LocalDateTime create_date;
    private String category;
}
