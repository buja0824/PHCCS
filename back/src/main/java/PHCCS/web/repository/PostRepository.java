package PHCCS.web.repository;

import PHCCS.domain.Post;

public interface PostRepository {
    int save(Long id, Post post);
}
