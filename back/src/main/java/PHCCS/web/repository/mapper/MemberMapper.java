package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyParam;
import PHCCS.web.repository.domain.MemberProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(@Param("id") Long id, @Param("memberModifyParam") MemberModifyParam memberModifyParam);

    Optional<MemberProfileDTO> findMemberById(@Param("id") Long id);
}
