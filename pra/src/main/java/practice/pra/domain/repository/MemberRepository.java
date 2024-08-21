package practice.pra.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import practice.pra.domain.Member;
import practice.pra.domain.repository.mapper.MemberMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final MemberMapper mapper;

    public void save(Member member){
        mapper.save(member);
    }

    public void update(Long id, MemberUpdateDto memberUpdateDto){
        mapper.update(id, memberUpdateDto);
    }

    public Optional<Member> findById(Long id){
        Optional<Member> member = mapper.findById(id);
        return member;
    }


    public Optional<Member> findByLoginId(String id){
        Optional<Member> findMember = mapper.findByLoginId(id);
        return findMember;
    }


    /**
    public String findLoginIdByNameAndEmail(String name, String email) {
        String loginIdByNameAndEmail = mapper.findLoginIdByNameAndEmail(name, email);
        return loginIdByNameAndEmail;
    }
    */

    public Optional<Member> findPwdByIdAndEmail(String id, String email) {
        Optional<Member> pwdByIdAndEmail = mapper.findPwdByIdAndEmail(id, email);
        return pwdByIdAndEmail;
    }

    public List<Member> findAll(MemberSearchCon memberSearchCon){
        List<Member> all = mapper.findAll(memberSearchCon);
        return all;
    }

}
