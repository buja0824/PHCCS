package PHCCS.web.service;

import PHCCS.domain.Comment;
import PHCCS.web.repository.CommentRepository;
import PHCCS.web.repository.domain.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;

    // 댓글 작성 완료 후 데이터베이스 저장 도중에 해당 게시글이 삭제가 되면?
    public ResponseEntity<?> save(String category, Long postId, Comment comment){
        comment.setPostId(postId);
        int save = repository.save(category, comment);
        if(save <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 저장에 오류가 발생하였습니다.");
        }else{
            return ResponseEntity.ok().body("댓글 저장 완료");
        }
    }

    public List<Comment> findAllComment(String category, Long postId){
        List<Comment> allComment = repository.findAllComment(category, postId);
        return allComment;
    }

    public void updateComment(String category, Long postId, Long commentId, CommentDto dto){
        dto.setCategory(category);
        dto.setPostId(postId);
        dto.setCommentId(commentId);
        repository.updateComment(dto);

    }

}
