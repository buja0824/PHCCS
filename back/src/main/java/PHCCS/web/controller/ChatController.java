package PHCCS.web.controller;

import PHCCS.domain.ChatRoom;
import PHCCS.web.service.ChatService;
import PHCCS.web.service.domain.ChatConnectDTO;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chat")
    public ChatRoom createChatRoom(@RequestBody ChatConnectDTO chatConnectDTO) {
        log.info("1. 채팅방을 생성합니다.");
        return chatService.createRoom(chatConnectDTO);
    }

    @GetMapping("/chat")
    public List<ChatRoom> findAllRoom() {
        return chatService.findAllRoom();
    }

    
}
