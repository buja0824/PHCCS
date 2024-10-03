package PHCCS.web.controller;

import PHCCS.domain.Member;
import PHCCS.jwt.JwtUtil;
import PHCCS.web.service.TokenService;
import PHCCS.web.service.domain.DuplicateCheckDto;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDTO;
import PHCCS.web.service.domain.MemberDTO;
import PHCCS.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        // 서비스에서 중복 체크
        DuplicateCheckDto duplicateCheckDto = service.isDuplicateMember(member.getEmail(), member.getNickName(), member.getPhoNo());

        // 중복 체크 후 처리
        if (duplicateCheckDto.isAnyDuplicate()) {
            StringBuilder message = new StringBuilder("회원가입 실패: ");
            if (duplicateCheckDto.isEmailDuplicate()) message.append("이메일 중복. ");
            if (duplicateCheckDto.isNickNameDuplicate()) message.append("닉네임 중복. ");
            if (duplicateCheckDto.isPhoNoDuplicate()) message.append("전화번호 중복.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message.toString());
        }

        // 회원 정보 저장
        boolean isSuccess = service.save(member);

        if(isSuccess){
            return ResponseEntity.ok("회원가입 되었습니다.");
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 저장 중 오류");
        }
    }

    @PostMapping("/auth/signin")
    public Map<String, String> login(@RequestBody MemberDTO memberDto) {
        return service.login(memberDto);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");

        boolean isSuccess = service.logout(actualToken);

        if(isSuccess) {
            return ResponseEntity.ok("로그아웃 되었습니다.");
        }else {return ResponseEntity.badRequest().body("잘못된 접근.");}
    }

    @PatchMapping("/member/update")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token
    , @RequestBody MemberModifyDTO ModifyDto){

        String actualToken = token.replace("Bearer ", "");

        int isSuccess = service.modifyMember(jwtUtil.extractSubject(actualToken), ModifyDto);

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
    public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String token){
        String actualToken = token.replace("Bearer ", "");
        int isSuccess = service.deleteMember(jwtUtil.extractSubject(actualToken));

        if(isSuccess == 1){
            return ResponseEntity.ok("회원탈퇴 되었습니다.");
        }else{return ResponseEntity.badRequest().body("회원탈퇴 오류");}
    }

    @GetMapping("/auth/refresh")
    public Map<String, String> refreshAccessToken(@RequestHeader("Authorization") String token){
        log.info("받은 RefreshToken: {}", token);
        String actualToken = token.replace("Bearer ", "");
        return tokenService.refreshAccessToken(actualToken);
    }

}
