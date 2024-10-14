package PHCCS.service.post;

import lombok.Data;

@Data
public class MyPostDTO {

    private Long id;
    private String title;
    private String createDate;
    private String updateDate;
    private String category;
}
