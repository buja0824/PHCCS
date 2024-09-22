package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(@Param("id") Long id, @Param("memberModifyDto") MemberModifyDto memberModifyDto);

    Optional<MemberProfileDTO> findMemberById(@Param("id") Long id);

    int deleteMember(@Param("id") Long id);

    int findRoleById(@Param("id") Long id);
}
