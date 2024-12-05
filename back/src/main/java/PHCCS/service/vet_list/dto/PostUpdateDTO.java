package PHCCS.service.vet_list.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostUpdateDTO {
    private String category;
    private String title;
    private String content;
    private LocalDateTime modifyTime = LocalDateTime.now();
}
