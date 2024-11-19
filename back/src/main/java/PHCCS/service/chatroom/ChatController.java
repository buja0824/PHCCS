package PHCCS.service.chatroom;

import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.sse.SSEService;
import PHCCS.service.chatroom.dto.ChatConnectDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final JwtUtil jwtUtil;
    private final ChatService chatService;
    private final SSEService sseService;

    @PostMapping("/chat")
    public ChatRoom createChatRoom(
            @RequestBody ChatConnectDTO chatConnectDTO,
            @RequestHeader("Authorization") String token) {

        Long loginMember = jwtUtil.extractSubject(token);

        chatConnectDTO.setCreateMemberId(loginMember);
        log.info("1. 채팅방을 생성합니다.");
        ChatRoom chatRoom = chatService.createRoom(chatConnectDTO);

        String roomId = chatRoom.getRoomId();
        String roomName = chatConnectDTO.getRoomName();
        // 방에 초대받은 사용자에게 알림 보내기
        sseService.inviteAlarm(chatConnectDTO.getParticipatingMemberId(), roomId, roomName, loginMember);
        return chatRoom;
    }

    @GetMapping("/chat")
    public List<ChatRoom> findAllRoom(@RequestHeader("Authorization") String token) {
        Long loginMember = jwtUtil.extractSubject(token);
        // 특정 사용자가 참여중인 방 보여주기
        return chatService.findAllRoom(loginMember);
    }
}

