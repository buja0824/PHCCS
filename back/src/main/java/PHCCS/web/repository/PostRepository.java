package PHCCS.web.repository;

import PHCCS.domain.Post;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostRepository {
    int communitySave(Long id, Post post);
    int qnaSave(Long id, Post post);
    int vetSave(Long id, Post post);

    Post showPost(String category, Long id);
    List<Post> showAllPost(String category);
}
