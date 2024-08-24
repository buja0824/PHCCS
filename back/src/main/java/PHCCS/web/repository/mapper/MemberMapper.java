package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    int save(Member member);

}
