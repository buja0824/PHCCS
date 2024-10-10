package PHCCS.service.member.repository;

import PHCCS.service.member.Member;
import PHCCS.service.member.dto.MemberModifyDTO;
import PHCCS.service.member.dto.MemberProfileDTO;

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
