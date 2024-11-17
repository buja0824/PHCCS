package PHCCS.service.chatroom.dto;

import lombok.Data;

@Data
public class ChatConnectDTO {
    private Long createMemberId;
    private Long participatingMemberId;
    private String roomName;
}
