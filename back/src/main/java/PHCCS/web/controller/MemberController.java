package PHCCS.web.controller;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberDto;
import PHCCS.web.repository.domain.SessionMemberDTO;
import PHCCS.web.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> add(@RequestBody Member member) {
        ResponseEntity<?> save = service.save(member);
        return save;
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto
    , HttpServletRequest request) {

        Member member = service.findMemberByEmail(memberDto.getEmail());

        if(member != null){
            ResponseEntity<?> login = service.login(member, memberDto);

            if(login.getStatusCode().is2xxSuccessful()){
                SessionMemberDTO sessionMember = new SessionMemberDTO(member.getEmail(), member.getPwd(), member.getRole());
                HttpSession session = request.getSession();
                session.setAttribute("loginMember", sessionMember);
                return ResponseEntity.ok("로그인 되었습니다.");
            }else{return ResponseEntity.badRequest().body("비밀번호를 다시 입력해주세요.");}
        }
        return ResponseEntity.badRequest().body("없는 회원입니다.");
    }
}
