package PHCCS.web.repository;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberModifyParam;

import java.util.Optional;

public interface MemberRepository {
    int save(Member member);

    Optional<Member> findMemberByEmail(String email);

    int modifyMember(long id, MemberModifyParam memberModifyParam);
}
