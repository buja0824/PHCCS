package PHCCS.web.repository;

import PHCCS.domain.Post;

public interface PostRepository {
    int communitySave(Long id, Post post);
    int qnaSave(Long id, Post post);
    int vetSave(Long id, Post post);
}
