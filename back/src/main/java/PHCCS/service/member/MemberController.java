package PHCCS.service.member;

import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.response.ApiResponse;
import PHCCS.service.member.dto.DuplicateCheckDTO;
import PHCCS.service.member.dto.MemberDTO;
import PHCCS.service.member.dto.MemberModifyDTO;
import PHCCS.service.member.dto.MemberProfileDTO;
import PHCCS.service.member.exception.LoginFailedException;
import PHCCS.service.member.token.TokenService;
import PHCCS.common.jwt.TokenStatus;
import PHCCS.common.jwt.TokenValidationException;

import PHCCS.service.vet.dto.VetDuplicateCheckDTO;
import PHCCS.service.vet.dto.VetRequestDTO;
import PHCCS.service.vet.dto.VetSignupDTO;
import PHCCS.service.vet.VetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;
    private final VetService vetService;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/signup/member")
    public ResponseEntity<?> add(@RequestBody Member member) {

        // 서비스에서 중복 체크
        DuplicateCheckDTO duplicateCheckDto = service.isDuplicateMember(member.getEmail(), member.getNickName(), member.getPhoNo());

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

    // 수의사 회원 가입
    @PostMapping("/auth/signup/vet")
    public ResponseEntity<?> addVet(@RequestBody VetSignupDTO vetSignupDTO) {
        log.info("MemberController - addVet 시작");
        log.info("addVet - VetSignupDTO: {}", vetSignupDTO);

        VetDuplicateCheckDTO vetDuplicateCheckDto = vetService.isDuplicateVet(vetSignupDTO.getEmail(), vetSignupDTO.getNickName(), vetSignupDTO.getPhoNo(), vetSignupDTO.getLicenseNo());

        // 중복 체크 후 처리
        if (vetDuplicateCheckDto.isAnyDuplicate()) {
            log.info("/auth/signup/vet - vetDuplicateCheckDto.isAnyDuplicate() 실행");
            StringBuilder message = new StringBuilder("수의사 회원가입 실패: ");
            if (vetDuplicateCheckDto.isEmailDuplicate()) message.append("이메일 중복. ");
            if (vetDuplicateCheckDto.isNickNameDuplicate()) message.append("닉네임 중복. ");
            if (vetDuplicateCheckDto.isPhoNoDuplicate()) message.append("전화번호 중복.");
            if (vetDuplicateCheckDto.isLicenseNoDuplicate()) message.append("면허번호 중복.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message.toString());
        }


        // 수의사 인증 요청 및 저장
        Boolean isSuccess = vetService.processSaveAndRequest(vetSignupDTO);

        if(isSuccess) {
            log.info("MemberController - addVet 완료");
            return ResponseEntity.ok("회원 가입이 완료되었습니다!\n" +
                    "현재 계정은 수의사 인증이 필요한 상태입니다. 인증 요청이 성공적으로 접수되었으며, 관리자가 확인하여 승인 또는 거절 처리를 하게 됩니다.");
        } else {
            log.info("MemberController - addVet 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 저장 중 오류");
        }
    }

// 해당 메소드는 이전에 JWT 에서 memberId를 가져오던 방식이랑 다르게
// JwtAuthenticaionFilter에서 생성한 인증객체에서 memberId를 가져오는 방식으로 만듬
// @AuthenticationPrincipal 어노테이션으로 인증객체에서 MemberId를 가져옴
    @PostMapping("/auth/reRequest")
    public  ResponseEntity<?> reRequestVetApproval(@RequestBody VetRequestDTO vetRequestDTO, @AuthenticationPrincipal Long memberId) {
        log.info("MemberController - reRequestVetApproval 실행");
        vetService.processVetRequestData(vetRequestDTO, memberId);
        // 성공 예외처리
        return ResponseEntity.ok("인증 요청이 성공적으로 접수되었으며, 관리자가 확인하여 승인 또는 거절 처리를 하게 됩니다.");
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
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,
                                    @RequestBody MemberModifyDTO modifyDto) {
        service.modifyMember(jwtUtil.extractSubject(token), modifyDto); // 서비스 호출
        return ApiResponse.successUpdate(); // 성공 응답
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

    //로그인 실패 처리 (401 Unauthorized)
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<String> handleLoginFailedException(LoginFailedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
    }
}
