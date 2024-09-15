package PHCCS.web.controller;

import PHCCS.domain.Member;
import PHCCS.jwt.JwtUtil;
import PHCCS.web.service.TokenService;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDto;
import PHCCS.web.service.domain.MemberDto;
import PHCCS.web.service.domain.SessionMemberDTO;
import PHCCS.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> add(@RequestBody Member member) {
        ResponseEntity<?> save = service.save(member);
        return save;
    }

    @PostMapping("/auth/signin")
    public Map<String, String> login(@RequestBody MemberDto memberDto) {
        return service.login(memberDto);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");

        Boolean isSuccess = service.logout(actualToken);

        if(isSuccess) {
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
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String token){
        String actualToken = token.replace("Bearer ", "");
        Optional<MemberProfileDTO> memberProfile = service.findMyProfileById(Long.valueOf(jwtUtil.extractSubject(actualToken)));

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

    @GetMapping("/auth/refresh")
    public Map<String, String> refreshAccessToken(@RequestHeader("Authorization") String token){
        String actualToken = token.replace("Bearer ", "");
        return tokenService.refreshAccessToken(actualToken);
    }

}
