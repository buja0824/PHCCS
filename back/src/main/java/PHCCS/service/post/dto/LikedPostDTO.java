package PHCCS.service.post.dto;

import lombok.Data;

@Data
public class LikedPostDTO {

    private Long id;
    private String nickName;
    private String title;
    private String partOfContent;
    private Long viewCnt;
    private Long likeCnt;
    private String createDate;
    private String updateDate;
    private String category;
}
