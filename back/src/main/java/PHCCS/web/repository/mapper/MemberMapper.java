package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
<<<<<<< HEAD
import PHCCS.web.repository.domain.MemberModifyDTO;
=======
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDto;
>>>>>>> 진구
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(@Param("id") Long id, @Param("memberModifyDto") MemberModifyDTO memberModifyDto);

    Optional<MemberProfileDTO> findMemberById(@Param("id") Long id);

    int deleteMember(@Param("id") Long id);

    int findRoleById(@Param("id") Long id);

    int existsByEmail(@Param("email") String email);

    int existsByNickName(@Param("nickName") String nickName);

    int existsByPhoNo(@Param("phoNo") String phoNo);
}
