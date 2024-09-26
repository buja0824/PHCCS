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

    /**
     * @param session
     * 채팅방에 첫 입장시 세션에 검증헤더에서 토큰을 파싱해서 어떤 회원인지 검증함
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("##세션 : {}", session);
        chatService.enterRoom(session);
    }

    /**
     * @param session
     * @param message
     * @throws Exception
     * 전달하는 메시지는 messageType,
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        chatService.handlerActions(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("WebSocket 연결이 닫힘 = {}", session.getId());
        chatService.closeSession(session, status);
    }

}
