package PHCCS.web.repository.mapper;

import PHCCS.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    int save(Long id, Post post);

}
