package PHCCS.service.member.repository;

import PHCCS.service.member.Member;
import PHCCS.service.member.dto.MemberModifyDTO;
import PHCCS.service.member.dto.MemberProfileDTO;
import PHCCS.service.member.repository.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisMemberRepository implements MemberRepository {

    final private MemberMapper mapper;

    @Override
    public int save(Member member) {
        log.info("MybatisMemberRepository - save 실행");
        int save = mapper.save(member);
        log.info("MybatisMemberRepository - save 완료");
        return save;
    }

    @Override
    public Optional<Member> findMemberByEmail(String email){
        Optional<Member> member = mapper.findMemberByEmail(email);
        return member;
    }

    @Override
    public int modifyMember(Long id, MemberModifyDTO memberModifyDto){
        int isSuccess = mapper.modifyMember(id, memberModifyDto);
        return isSuccess;
    }

    @Override
    public Optional<MemberProfileDTO> findMemberProfileById(Long id){
        Optional<MemberProfileDTO> memberProfile = mapper.findMemberProfileById(id);
        return memberProfile;
    }

    @Override
    public Optional<Member> findMemberById(Long id) {
        Optional<Member> member = mapper.findMemberById(id);
        return member;
    }

    @Override
    public int deleteMember(Long id){
        int isSuccess = mapper.deleteMember(id);

        return isSuccess;
    }

    @Override
    public int findRoleById(Long id) {
        return mapper.findRoleById(id);
    }

    @Override
    public int existsByEmail(String email) {
        return mapper.existsByEmail(email);
    }

    @Override
    public int existsByNickname(String nickName) {
        return mapper.existsByNickName(nickName);
    }

    @Override
    public int existsByPhoNo(String phoNo) {
        return mapper.existsByPhoNo(phoNo);
    }
}
