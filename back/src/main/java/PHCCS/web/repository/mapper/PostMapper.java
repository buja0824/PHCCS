package PHCCS.web.repository.mapper;

import PHCCS.domain.Post;
import PHCCS.web.repository.domain.PostModifyParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    /**
     * 일반 게시글을 처리하는 영역
     */
    int communitySave(@Param("post") Post post);
    /**
     * 질문 게시글을 처리하는 영역
     */
    int qnaSave(@Param("post") Post post);
    /**
     * 수의사 게시글을 처리하는 영역
     */
    int vetSave(@Param("post")Post post);

    Post showPost(@Param("category") String category, @Param("id") Long id);

    /**
     *  카테고리별 모든 게시글을 가져온다
     */
    List<Post> showAllPost(@Param("category") String category);
    void modifyPost(@Param("memberId") Long memberId, @Param("postId") Long postId, @Param("param")PostModifyParam param, @Param("dir") String fileDir);
    void deletePost(@Param("category") String category, @Param("memberId") Long memberId, @Param("postId") Long postId);
    String findPostDir(@Param("category") String category, @Param("id") Long postId);
}
