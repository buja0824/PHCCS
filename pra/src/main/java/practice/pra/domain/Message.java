package practice.pra.domain;

import lombok.Data;

@Data
public class Message {

    private Long senderId;
    private Long receiveId;

    private String content;
}
