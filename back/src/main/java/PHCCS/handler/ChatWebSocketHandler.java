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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("##세션 : {}", session);
        chatService.enterRoom(session);
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        chatService.handlerActions(session, message);
    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
//        log.info("소켓 닫기");
//        chatService.se
//    }

}
