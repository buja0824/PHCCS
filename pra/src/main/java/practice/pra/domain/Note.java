package practice.pra.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Note {
    private Long noteId;    // 쪽지방 id
    private Long senderId;    // 쪽지 보내는 사람
    private Long receiverId;   // 쪽지 받는 사람
    private String content;     // 쪽지 내용
    private LocalDateTime sendDate = LocalDateTime.now(); // 쪽지 작성 시간
    private Long readChk;
}
