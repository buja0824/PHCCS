package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(@Param("id") Long id, @Param("memberModifyDto") MemberModifyDTO memberModifyDto);

    Optional<Member> findMemberById(@Param("id") Long id);
}
