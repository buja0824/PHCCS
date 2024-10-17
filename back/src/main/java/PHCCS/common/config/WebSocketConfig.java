package PHCCS.common.config;

import PHCCS.common.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("웹 소켓 연결 대기");
        // ws://localhost:3030/ws/chat 요청시 소켓 통신으로 업그래이드
        registry
                .addHandler(webSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");

    }
}
