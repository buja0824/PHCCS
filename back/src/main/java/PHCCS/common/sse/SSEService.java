package PHCCS.common.sse;

import PHCCS.service.comment.Comment;
import PHCCS.service.comment.dto.CommentAddDTO;
import PHCCS.service.post.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public void add(Long memberId, SseEmitter emitter){
        sseEmitterMap.put(memberId, emitter);
        emitter.onCompletion(()-> {
                sseEmitterMap.remove(memberId);
                log.info("연결이 끊어짐");
                emitter.complete();
            });

        emitter.onTimeout(()->{
                sseEmitterMap.remove(memberId);
                log.info("타임아웃 {}", memberId);
                emitter.complete();

            });
    }

    public void inviteAlarm(Long participantId, String chatRoomId){
        try {
            SseEmitter emitter = sseEmitterMap.get(participantId);

            emitter
                    .send(SseEmitter.event()
                    .name("inviteMsg")
                    .data("새로운 채팅방에 초대 되었습니다." + chatRoomId));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 알림 보낼 사용자 찾고, 알림 보낼 내용은 댓글이 등록되었습니다. 작성자 : 댓글내용
     * 알림 보낼 사용자 찾는건 게시글 작성자 찾는것과 동일
     */
    public void addCommentAlarm(String category, Long postId, CommentAddDTO comment){
        Long authorId = postRepository.findAuthorId(category, postId);
        try {
            sseEmitterMap.get(authorId).send(SseEmitter.event()
                            .name("새로운 댓들이 등록되었습니다.")
                            .data(comment.getNickName() +": " +comment.getComment()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

}
