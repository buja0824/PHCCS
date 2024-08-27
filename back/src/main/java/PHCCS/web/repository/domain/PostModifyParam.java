package PHCCS.web.repository.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostModifyParam {
    private String category;
    private String title;
    private String content;
    private LocalDateTime modifyTime = LocalDateTime.now();
}
