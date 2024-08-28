package PHCCS.web.service;

import PHCCS.domain.Member;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.repository.domain.MemberDto;
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

    public ResponseEntity<?> login(MemberDto memberDto) {
        Optional<Member> findMemberOptional = repository.findMemberByEmail(memberDto.getEmail());
        if (findMemberOptional.isPresent()) {
            Member m = findMemberOptional.get();
            // 비밀번호가 일치하는지 확인
            if (m.getPwd().equals(memberDto.getPwd())) {
                return ResponseEntity.ok("정상적으로 로그인 되었습니다.");
            }
            else{return ResponseEntity.badRequest().body("비밀번호를 다시 입력해주세요.");}
        }
        return ResponseEntity.badRequest().body("없는 아이디입니다.");
    }
}
