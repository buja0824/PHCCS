package PHCCS.web.service;

import PHCCS.domain.Member;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDto;
import PHCCS.web.service.domain.MemberDto;
import PHCCS.web.service.domain.SessionMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;

    public ResponseEntity<?> save(Member member) {
        // 현재 시간을 기록
        LocalDate currentDate = LocalDate.now();
        member.setCreated(currentDate);

        int resultRow = repository.save(member);

        if (resultRow < 0) {
            return ResponseEntity.badRequest().body("회원가입에 실패하였습니다.");
        }
        return ResponseEntity.ok("정상적으로 가입 되었습니다.");
    }

    public Optional<Member> findMemberByEmail(String email){
        Optional<Member> findMemberOptional = repository.findMemberByEmail(email);
        if (findMemberOptional.isPresent()) {
            return Optional.of(findMemberOptional.get());}
        else {return Optional.empty();}
    }
// service 계층에서 ResponseBody 반환하는것을 최대한 제한하고자 함
    public Optional<SessionMemberDTO> login(Member member, MemberDto memberDto) {

            SessionMemberDTO sessionMember = new SessionMemberDTO();

            //비밀번호 확인
        if (member.getPwd().equals(memberDto.getPwd())) {
            // 맞다면 sessionMember 필드값 설정
            sessionMember.setId(member.getId());
            sessionMember.setEmail(member.getEmail());
            sessionMember.setPwd(member.getPwd());
            sessionMember.setRole(member.getRole());
            return Optional.of(sessionMember);
        }
        return Optional.empty();
    }

    public int modifyMember (Long id, MemberModifyDto memberModifyDto){
        int isSuccess = repository.modifyMember(id, memberModifyDto);

        return isSuccess;
    }

    public Optional<MemberProfileDTO> findMyProfileById(Long id) {
        Optional<MemberProfileDTO> memberProfile = repository.findMemberById(id);

        return memberProfile;
    }

    public int deleteMember(Long id) {
        int isSuccess = repository.deleteMember(id);

        return isSuccess;
    }
}

