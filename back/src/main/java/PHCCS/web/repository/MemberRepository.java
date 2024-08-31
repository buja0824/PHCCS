package PHCCS.web.repository;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyDto;

import java.util.Optional;

public interface MemberRepository {
    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(Long id, MemberModifyDto memberModifyDto);

    Optional<Member> findMemberById(Long id);
}
