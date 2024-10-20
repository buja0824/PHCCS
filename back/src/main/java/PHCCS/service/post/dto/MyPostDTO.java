package PHCCS.service.post.dto;

import lombok.Data;

@Data
public class MyPostDTO {

    private Long id;
    private String title;
    private Long viewCnt;
    private Long likeCnt;
    private String createDate;
    private String updateDate;
    private String category;
}
