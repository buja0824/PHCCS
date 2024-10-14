package PHCCS.common.sse;

import PHCCS.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
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
    public ResponseEntity<SseEmitter> connectSSE(@RequestHeader("Authorization") String token){
        log.info("sse connect");
        log.info("token = {}", token);
        Long memberId = jwtUtil.extractSubject(token);
        SseEmitter emitter = new SseEmitter(1000*1000L);
        sseService.add(memberId, emitter);
        try {
            emitter.send(SseEmitter.event()
                    .name("initConnect")
                    .data("connect!!"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("이거 나옴?");
        return ResponseEntity.ok().body(emitter);
    }
//
//    private static Long exMemberId(HttpServletRequest request) {
//        String SECRET_KEY = "OapJ2D0zLQs4S1FdY5TgRhYKJffpMq7RaNmbN4XURRs";
//
//        String authorization = request.getHeader("Authorization");
//        String token = authorization.substring(7);
//        // 토큰에서 Claims 추출
//        Claims payload = Jwts.parserBuilder()
//                .setSigningKey(SECRET_KEY)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        // Claims에서 id 값 추출
//        Long entryId = Long.parseLong(payload.get("id").toString());
//        return entryId;
//    }
}
