package PHCCS.web.repository.mapper;

import PHCCS.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostMapper {
    /**
     * 일반 게시글을 처리하는 영역
     */
    int communitySave(@Param("memberId") Long memberId, @Param("post") Post post);


    /**
     * 질문 게시글을 처리하는 영역
     */

    int qnaSave(@Param("memberId")Long memberId, @Param("post")Post post);


    /**
     * 수의사 게시글을 처리하는 영역
     */
    int vetSave(@Param("memberId")Long memberId, @Param("post")Post post);

}
