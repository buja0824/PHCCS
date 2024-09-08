package PHCCS.handler;

import PHCCS.domain.ChatRoom;
import PHCCS.domain.Message;
import PHCCS.web.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService chatService;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 메시ㅣㅈ 가져오기
        log.info("Message received: {}", payload);
        // payload를 객체로 변환
        Message chatMessage = mapper.readValue(payload, Message.class);

        ChatRoom chatRoom = chatService.findRoomById(chatMessage.getRoomId());

        chatRoom.handlerActions(session, chatMessage, chatService);

    }


}
