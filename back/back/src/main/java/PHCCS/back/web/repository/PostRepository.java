package PHCCS.back.web.repository;

import PHCCS.back.domain.Post;
import org.springframework.http.ResponseEntity;

public interface PostRepository {
    int save(Post post);
}
