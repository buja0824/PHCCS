package PHCCS.web.repository;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyParam;
import PHCCS.web.repository.domain.MemberProfileDTO;

import java.util.Optional;

public interface MemberRepository {
    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(Long id, MemberModifyParam memberModifyParam);

    Optional<MemberProfileDTO> findMemberById(Long id);
}
