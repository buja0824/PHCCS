package PHCCS.web.service;

import PHCCS.domain.Member;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.repository.domain.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    public Member findMemberByEmail(String email){
        Optional<Member> findMemberOptional = repository.findMemberByEmail(email);
        if (findMemberOptional.isPresent()) {
            return findMemberOptional.get();}
        else {return null;}
    }

    public ResponseEntity<?> login(Member member, MemberDto memberDto) {
            // 비밀번호가 일치하는지 확인
            if (member.getPwd().equals(memberDto.getPwd())) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else{return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
    }
}

