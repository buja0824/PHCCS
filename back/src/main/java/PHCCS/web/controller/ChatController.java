package PHCCS.web.controller;

import PHCCS.domain.ChatRoom;
import PHCCS.web.service.ChatService;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ChatRoom createChatRoom(@RequestBody String name) {
        return chatService.createRoom(name);
    }

    @GetMapping
    public List<ChatRoom> findAllRoom() {
        return chatService.findAllRoom();
    }
}
