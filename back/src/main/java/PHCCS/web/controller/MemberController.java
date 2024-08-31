package PHCCS.web.controller;

import PHCCS.domain.Member;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDto;
import PHCCS.web.service.domain.MemberDto;
import PHCCS.web.service.domain.SessionMemberDTO;
import PHCCS.web.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        //1. POST 요청으로 받은 email과 일치하는 멤버 객체를 찾음
        Optional<Member> optionalMember = service.findMemberByEmail(memberDto.getEmail());
        //2-1. 찾았다면 3 이행
        if(optionalMember.isPresent()){
            //3. MermerSevice 계층의 login 메서드에서 sessionMember 객체 호출
            Optional<SessionMemberDTO> sessionMember = service.login(optionalMember.get(), memberDto);
            //4-1. 필드값이 설정되어있는 sessionMember를 받았다면 5 이행
            if(sessionMember.isPresent()){
                //5. 세션 설정
                HttpSession session = request.getSession();
                session.setAttribute("loginMember", sessionMember.get());
                return ResponseEntity.ok("로그인 되었습니다.");
            }
            //4-2. 못 찾았다면 다음 문장 실행
            else{return ResponseEntity.badRequest().body("비밀번호를 다시 입력해주세요.");}
        //2-2. 못 찾았다면 다음 문장 실행
        }else{return ResponseEntity.badRequest().body("없는 회원입니다.");}
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
            return ResponseEntity.ok("로그아웃 되었습니다.");
        }else {return ResponseEntity.badRequest().body("잘못된 접근.");}
    }

    @PatchMapping("/member/update")
    public ResponseEntity<?> update(@SessionAttribute(name = "loginMember", required = false) SessionMemberDTO loginMember
    , @RequestBody MemberModifyDto ModifyDto){

        int isSuccess = service.modifyMember(loginMember.getId(), ModifyDto);

        if(isSuccess == 1){
            return ResponseEntity.ok("수정 되었습니다.");
        }else{return ResponseEntity.badRequest().body("수정 중 오류가 발생했습니다.");}
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> getMyProfile(@SessionAttribute(name = "loginMember", required = false) SessionMemberDTO loginMember){
        Optional<MemberProfileDTO> memberProfile = service.findMyProfileById(loginMember.getId());

        if(memberProfile.isPresent()) {
            return ResponseEntity.ok(memberProfile.get());
        }else{return ResponseEntity.badRequest().body("정보 조회 오류");}
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> deleteMember(@SessionAttribute(name = "loginMember", required = false) SessionMemberDTO loginMember){
        int isSuccess = service.deleteMember(loginMember.getId());

        if(isSuccess == 1){
            return ResponseEntity.ok("회원탈퇴 되었습니다.");
        }else{return ResponseEntity.badRequest().body("회원탈퇴 오류");}
    }
}
