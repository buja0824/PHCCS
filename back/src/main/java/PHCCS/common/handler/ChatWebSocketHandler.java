package PHCCS.common.handler;

import PHCCS.service.chatroom.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    //TextWebSocketHandler 는 AbstractWebSocketHandler 를 상속 받음 그 클래스의 메서드들을 현재 오버라이딩 한거고
    //TextWebSocketHandler 를 명시적으로 적은 이유는 바이너리 데이터를 처리하는 handleBinaryMessage 를 추후에 추가할 예정이었어서
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
     */
    @Override
    protected void handleTextMessage(
            WebSocketSession session, TextMessage message) throws Exception {
        chatService.handlerActions(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("WebSocket 연결이 닫힘 = {}", session.getId());
        chatService.closeSession(session, status);
    }

}
