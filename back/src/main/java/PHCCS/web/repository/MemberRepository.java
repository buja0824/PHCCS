package PHCCS.web.repository;

import PHCCS.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    int save(Member member);

    Optional<Member> findMemberByEmail(String email);
}
