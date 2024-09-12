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

    private final ObjectMapper mapper;
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("##세션 : {}", session);
        log.info("##세션.getLocalAddress() : {}", session.getLocalAddress());
        log.info("##세션.getRemoteAddress() : {}", session.getRemoteAddress());
        log.info("##세션.getHandshakeHeaders() : {}", session.getHandshakeHeaders());
        log.info("##세션.getAcceptedProtocol() : {}", session.getAcceptedProtocol());
        log.info("##세션.getAttributes() : {}", session.getAttributes());
        log.info("##세션.getId() : {}", session.getId());

    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 메시지 가져오기
        log.info("3. 받은 메시지: {}", payload);

        // payload를 객체로 변환
        Message chatMessage = mapper.readValue(payload, Message.class);
        chatMessage.setTimestamp(LocalDateTime.now());
        // 생성된 방 찾기
        // 방 아이디는 message 안에 들어있음
        ChatRoom chatRoom = chatService.findRoomById(chatMessage.getRoomId());
        if(chatRoom == null) {
            log.info("채팅방이 존재 하지 않음");
            return;
        }
        log.info("방을 찾음 : {}", chatRoom);
        chatService.handlerActions(session, chatMessage);
    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
//        log.info("소켓 닫기");
//        chatService.se
//    }

}
