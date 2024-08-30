package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(@Param("id") long id, @Param("memberModifyParam") MemberModifyParam memberModifyParam);
}
