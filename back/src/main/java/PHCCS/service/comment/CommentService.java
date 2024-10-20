package PHCCS.service.comment;

import PHCCS.service.comment.dto.CommentDTO;
import PHCCS.service.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;

    // 댓글 작성 완료 후 데이터베이스 저장 도중에 해당 게시글이 삭제가 되면?
    public boolean save(String category, Long postId, Comment comment){
        comment.setPostId(postId);
        comment.setLikeCnt(0L);
        int save = repository.save(category, comment);
        return save > 0;
    }

    public List<Comment> findAllComment(String category, Long postId){
        List<Comment> allComment = repository.findAllComment(category, postId);
        return allComment;
    }

    public void updateComment(String category, Long postId, Long commentId, CommentDTO dto){
        dto.setCategory(category);
        dto.setPostId(postId);
        dto.setCommentId(commentId);
        repository.updateComment(dto);
    }

    public void deleteComment(String category, Long postId, Long commentId){
        repository.deleteComment(category, postId, commentId);
    }

    @Transactional
    public boolean incrementLike(Long memberId, String category, Long postId, Long commentId) {
        log.info("|se|incrementLike()");

        Boolean likeMember = repository.isLikeMember(memberId, category, postId, commentId);
        log.info("|se|likeMember = {}", likeMember);
        if (likeMember == null || !likeMember){
            repository.incrementLike(category, postId, commentId);
            repository.likeMember(memberId, category, postId, commentId);
            return true;
        }
        else{
            log.info("이미 좋아요를 누름" );
            return false;
        }
    }

}
