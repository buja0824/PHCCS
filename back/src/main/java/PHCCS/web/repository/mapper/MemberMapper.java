package PHCCS.web.repository.mapper;

import PHCCS.domain.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    Optional<Member> findMemberByEmail(String email);
}
