package PHCCS.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    public enum MessageType{
        ENTER, TALK
        //처음 입장인지 아닌지 구별하는 Enum
    }
    private MessageType type;
    private String roomId;
    private String message;
    private String sender;
    private Date timestamp;

}
