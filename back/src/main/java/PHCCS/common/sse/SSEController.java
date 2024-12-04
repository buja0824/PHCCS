package PHCCS.common.sse;

import PHCCS.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SSEController {

    private final JwtUtil jwtUtil;
    private final SSEService sseService;

    @GetMapping(value = "/connect-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSSE(@RequestHeader("Authorization") String token){
        log.info("sse 연결시작");

        Long memberId = jwtUtil.extractSubject(token);
        log.info("sse 연결을 위한 멤버 ID = {}", memberId);

        SseEmitter emitter = new SseEmitter(1000*3600L);

        sseService.add(memberId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .id(memberId+"")
                    .name("initConnect")
                    .data("connect!!"));

            log.info("memberId:{}",memberId);
            log.info("event:{}","initConnect");
            log.info("data:{}","connect");

            log.info("sse  연결을 위한 더미 데이터 전송 완료");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return emitter;
    }

}
