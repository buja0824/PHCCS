package PHCCS.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSEService {

    private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public void add(Long memberId, SseEmitter emitter){
        sseEmitterMap.put(memberId, emitter);
        emitter.onCompletion(()-> {
            log.info("연결이 끊어지는가");
                sseEmitterMap.remove(memberId);
                log.info("연결이 끊어짐");
            });
        emitter.onTimeout(()->{
                log.info("타임아웃 예정 멤버 {}", memberId);
                sseEmitterMap.remove(memberId);
                log.info("타임아웃");
            });
    }

    public void alarm(Long participantId, String chatRoomId){
        try {
            sseEmitterMap.get(participantId).send(SseEmitter.event()
                    .name("inviteMsg")
                    .data("새로운 채팅방에 초대 되었습니다." + chatRoomId));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
