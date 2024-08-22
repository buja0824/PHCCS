package PHCCS.memberservice.web.service;

import PHCCS.memberservice.domain.Member;
import PHCCS.memberservice.web.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;

    public ResponseEntity<?> save(Member member) {
        int resultRow = repository.save(member);
        if (resultRow < 0) {
            return ResponseEntity.badRequest().body("멤버 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok("정상적으로 등록 되었습니다.");
    }
}
