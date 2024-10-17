package PHCCS.service.post.dto;

import lombok.Data;

@Data
public class PostHeaderDTO {

    private Long id;

    private Long memberId;
    private String nickName;
    private String title;
    private Long viewCnt;
    private String createDate;
    private String updateDate;
}
