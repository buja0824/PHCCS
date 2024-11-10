package PHCCS.service.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentAddDTO {
    private String comment;
    private String nickName;
    private LocalDateTime writeTime = LocalDateTime.now();
}
