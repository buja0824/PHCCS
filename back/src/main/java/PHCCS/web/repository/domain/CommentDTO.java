package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class CommentDTO {
    private String category;
    private Long postId;
    private Long commentId;
    private String comment;
}
