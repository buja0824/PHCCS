package PHCCS.service.comment;

import PHCCS.service.comment.dto.CommentAddDTO;
import PHCCS.service.comment.dto.CommentDTO;
import PHCCS.service.comment.dto.MyCommentDTO;
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
    public boolean save(Long loginMember, String category, Long postId, CommentAddDTO comment){
//        comment.setPostId(postId);
//        comment.setLikeCnt(0L);
        int save = repository.save(loginMember, category, postId, comment);
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

    public List<MyCommentDTO> showLikedComments(Long memberId){

        List<MyCommentDTO> likedComments = repository.showLikedComments(memberId);
        for (MyCommentDTO likedComment : likedComments) {
            log.info("myComments = {}", likedComment);
        }
        return likedComments;
    }

    @Transactional
    public String incrementLike(Long memberId, String category, Long postId, Long commentId) {
        log.info("|se|incrementLike()");

        Boolean likeMember = repository.isLikeMember(memberId, category, postId, commentId);
        log.info("댓글에 좋아요 누른 멤버인가 = {}", likeMember);

        if (likeMember == null || !likeMember){
            repository.incrementLike(category, postId, commentId);
            log.info("좋아요 올리기");
            repository.likeMember(memberId, category, postId, commentId);
            log.info("좋아요한 사람으로 추가");
            return "좋아요 올리기";
        }
        else{
            repository.decrementLike(category, postId, commentId);
            log.info("좋아요 내리기" );
            repository.unLikeMember(memberId, category, postId, commentId);
            log.info("좋아요한 사람에서 제거");
            return "좋아요 내리기";
        }
    }

}
