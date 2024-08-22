package PHCCS.back.web.repository.mapper;

import PHCCS.back.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    int save(Post post);

}
