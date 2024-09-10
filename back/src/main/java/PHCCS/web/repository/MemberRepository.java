package PHCCS.web.repository;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyDTO;

import java.util.Optional;

public interface MemberRepository {
    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(Long id, MemberModifyDTO memberModifyDto);

    Optional<Member> findMemberById(Long id);
}
