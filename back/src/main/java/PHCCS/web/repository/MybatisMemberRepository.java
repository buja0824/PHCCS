package PHCCS.web.repository;

import PHCCS.domain.Member;
import PHCCS.web.repository.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisMemberRepository implements MemberRepository{

    final private MemberMapper mapper;

    @Override
    public int save(Member member) {
        int save = mapper.save(member);
        return save;
    }

    @Override
    public Optional<Member> findMemberByEmail(String email){
        Optional<Member> member = mapper.findMemberByEmail(email);
        return member;
    }
}
