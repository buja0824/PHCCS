package PHCCS.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {

    public enum MessageType{
        ENTER,
        TALK
    }
    private MessageType type;
    private Long memberId;
    private String message;

    private LocalDateTime timestamp;

}
