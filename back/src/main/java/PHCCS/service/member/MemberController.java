package PHCCS.service.member;

import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.jwt.TokenStatus;
import PHCCS.common.jwt.TokenValidationException;
import PHCCS.service.member.dto.DuplicateCheckDto;
import PHCCS.service.member.dto.MemberDTO;
import PHCCS.service.member.dto.MemberModifyDTO;
import PHCCS.service.member.dto.MemberProfileDTO;
import PHCCS.service.member.token.TokenService;
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

        boolean isSuccess = service.logout(token);

        if(isSuccess) {
            return ResponseEntity.ok("로그아웃 되었습니다.");
        }else {return ResponseEntity.badRequest().body("로그아웃 실패(refreshToken 삭제 실패)");}
    }

    @PatchMapping("/member/update")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token
            , @RequestBody MemberModifyDTO ModifyDto){

        int isSuccess = service.modifyMember(jwtUtil.extractSubject(token), ModifyDto);

        if(isSuccess == 1){
            return ResponseEntity.ok("수정 되었습니다.");
        }else{return ResponseEntity.badRequest().body("수정 중 오류가 발생했습니다.");}
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String token){
        Optional<MemberProfileDTO> memberProfile = service.findMyProfileById(Long.valueOf(jwtUtil.extractSubject(token)));

        if(memberProfile.isPresent()) {
            return ResponseEntity.ok(memberProfile.get());
        }else{return ResponseEntity.badRequest().body("정보 조회 오류");}
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String token){
        int isSuccess = service.deleteMember(jwtUtil.extractSubject(token));

        if(isSuccess == 1){
            return ResponseEntity.ok("회원탈퇴 되었습니다.");
        }else{return ResponseEntity.badRequest().body("회원탈퇴 오류");}
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestHeader("Authorization") String token) {
        log.info("받은 RefreshToken: {}", token);

        try {
            // 서비스 계층에서 토큰 갱신 로직 수행
            Map<String, String> tokens = tokenService.refreshAccessToken(token);
            return ResponseEntity.ok(tokens); // 상태 코드 200 OK와 함께 갱신된 토큰 반환

        } catch (TokenValidationException e) {
            // 토큰 검증에 실패했을 때, 예외의 상태에 따라 적절한 응답 처리
            if (e.getStatus() == TokenStatus.EXPIRED) {
                // 리프레시 토큰이 만료된 경우 401 상태 코드 반환
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "리프레시 토큰이 만료되었습니다. 다시 로그인하세요."));
                // 로그인 유도해야함
            } else if (e.getStatus() == TokenStatus.INVALID) {
                // 유효하지 않은 리프레시 토큰인 경우 401 상태 코드 반환
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "유효하지 않은 리프레시 토큰입니다."));
            } else {
                // 기타 검증 실패의 경우 400 상태 코드 반환
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "잘못된 리프레시 토큰 형식입니다."));
            }
        } catch (Exception e) {
            // 예기치 못한 서버 오류의 경우 500 상태 코드 반환
            log.error("토큰 갱신 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }
}
