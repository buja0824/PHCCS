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
    // 멤버 아이디 필요 없는거 같은데 왜 다시 넣었는지 기억이 안나
//    private Long memberId;
    private String message;

    private LocalDateTime timestamp;

}
