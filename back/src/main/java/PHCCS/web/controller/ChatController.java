package PHCCS.web.controller;

import PHCCS.domain.ChatRoom;
import PHCCS.web.service.ChatService;
import PHCCS.web.service.domain.ChatConnectDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private static final String SECRET_KEY = "OapJ2D0zLQs4S1FdY5TgRhYKJffpMq7RaNmbN4XURRs";
    private final ChatService chatService;

    @PostMapping("/chat")
    public ChatRoom createChatRoom(
            @RequestBody ChatConnectDTO chatConnectDTO,
            HttpServletRequest request) {
        Long memberId = exMemberId(request);
        chatConnectDTO.setCreateMemberId(memberId);
        log.info("1. 채팅방을 생성합니다.");
        return chatService.createRoom(chatConnectDTO);
    }

    @GetMapping("/chat")
    public List<ChatRoom> findAllRoom(HttpServletRequest request) {
        Long memberId = exMemberId(request);
        return chatService.findAllRoom(memberId);
    }

    private static Long exMemberId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = authorization.substring(7);
        // 토큰에서 Claims 추출
        Claims payload = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Claims에서 id 값 추출
        Long entryId = Long.parseLong(payload.get("id").toString());
        return entryId;
    }
}

