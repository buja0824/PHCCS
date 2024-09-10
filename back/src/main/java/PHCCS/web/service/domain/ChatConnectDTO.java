package PHCCS.web.service.domain;

import lombok.Data;

@Data
public class ChatConnectDTO {
    private Long createMemberId;
    private Long participatingMemberId;
}
