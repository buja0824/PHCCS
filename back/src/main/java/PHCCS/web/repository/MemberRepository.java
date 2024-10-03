package PHCCS.web.repository;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyDTO;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDTO;

import java.util.Optional;

public interface MemberRepository {
    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(Long id, MemberModifyDTO memberModifyDto);

    Optional<MemberProfileDTO> findMemberProfileById(Long id);

    Optional<Member> findMemberById(Long id);

    int deleteMember(Long id);

    int findRoleById(Long id);

    int existsByEmail(String email);

    int existsByNickname(String nickname);

    int existsByPhoNo(String phoNo);
}
