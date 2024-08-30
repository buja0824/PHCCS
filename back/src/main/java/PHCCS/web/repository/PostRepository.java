package PHCCS.web.repository;

import PHCCS.domain.Post;
import PHCCS.web.repository.domain.PostModifyParam;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostRepository {
    int communitySave(Post post);
    int qnaSave(Post post);
    int vetSave(Post post);

    Post showPost(String category, Long postId);
    List<Post> showAllPost(String category);
    void modifyPost(Long memberId, Long postId, PostModifyParam param, String fileDir);
    void deletePost(String category, Long memberId, Long postId);
    String findPostDir(String category, Long postId);
}
