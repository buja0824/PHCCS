package PHCCS.service.member;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.common.exception.InternalServerEx;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.service.member.dto.DuplicateCheckDTO;
import PHCCS.service.member.dto.MemberDTO;
import PHCCS.service.member.dto.MemberModifyDTO;
import PHCCS.service.member.dto.MemberProfileDTO;
import PHCCS.service.member.exception.LoginFailedException;
import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.member.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public boolean save(Member member) {
        // 현재 시간을 기록
        LocalDate currentDate = LocalDate.now();
        member.setCreated(currentDate);

        Boolean isSaveSuccess  = repository.save(member) > 0;

        return isSaveSuccess;
    }

    public Optional<Member> findMemberByEmail(String email){
        Optional<Member> findMemberOptional = repository.findMemberByEmail(email);
        if (findMemberOptional.isPresent()) {
            return Optional.of(findMemberOptional.get());}
        else {return Optional.empty();}
    }

    /** // 세션기반 로그인
     // service 계층에서 ResponseBody 반환하는것을 최대한 제한하고자 함
     public Optional<SessionMemberDTO> login(Member member, MemberDTO memberDto) {

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
     */
    // JWT 기반 로그인
    public Map<String, String> login(MemberDTO memberDto){
        Optional<Member> optionalMember = findMemberByEmail(memberDto.getEmail());

        if(!optionalMember.isPresent()) {
            throw new LoginFailedException("아이디가 틀렸습니다.");
        }

        Member member = optionalMember.get();

        if(!member.getPwd().equals(memberDto.getPwd())){
            throw new LoginFailedException("비번이 틀렸습니다.");
        }

        Map<String, String> tokens = new HashMap<>();
        String newAccessToken = jwtUtil.createAccessToken(member.getId(), member.getRole());
        tokens.put("accessToken", newAccessToken);
         log.info("accestokenId: {}", jwtUtil.extractId(newAccessToken));
        // log.info("accestokenrole: {}", jwtUtil.extractRole(newAccessToken));
        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        tokenService.storeRefreshToken(jwtUtil.extractId(refreshToken), jwtUtil.actual(refreshToken));
        tokens.put("refreshToken", refreshToken);

        // [LOG] 토큰 발급 확인
        log.info("보내는 토큰: {}", tokens);

        return tokens;
    }

    public boolean logout(String token){
        log.info("memberService - logout 토큰 id : {}", jwtUtil.extractId(token));
        return tokenService.removeRefreshToken(jwtUtil.extractId(token), jwtUtil.actual(token));
    }

    public void modifyMember(Long id, MemberModifyDTO memberModifyDto) {
        if (id == null || id <= 0) {
            throw new BadRequestEx("회원 ID가 유효하지 않습니다.");
        }

        if (memberModifyDto == null) {
            throw new BadRequestEx("수정할 정보가 제공되지 않았습니다.");
        }

        // 닉네임 변경 로직
        if (memberModifyDto.getNickname() != null && memberModifyDto.getPwd() == null) {
            boolean isNickNameDuplicate = isNicknameDuplicate(memberModifyDto.getNickname());
            if (isNickNameDuplicate) {
                throw new BadRequestEx("이미 사용 중인 닉네임입니다.");
            }

            int isSuccess = repository.updateNickname(id, memberModifyDto.getNickname());
            if (isSuccess == 0) {
                throw new InternalServerEx("닉네임 수정에 실패했습니다.");
            }
        }
        // 비밀번호 변경 로직
        else if (memberModifyDto.getPwd() != null && memberModifyDto.getCurrentPwd() != null) {
            // 1. 데이터베이스에서 현재 비밀번호 조회
            String currentPwd = repository.findPwdById(id);
            if (currentPwd == null) {
                throw new InternalServerEx("사용자의 비밀번호를 확인할 수 없습니다.");
            }

            // 2. 현재 비밀번호 검증
            if (!memberModifyDto.getCurrentPwd().equals(currentPwd)) {
                throw new BadRequestEx("현재 비밀번호가 일치하지 않습니다.");
            }

            // 3. 비밀번호 업데이트
            int isSuccess = repository.updatePwd(id, memberModifyDto.getPwd());
            if (isSuccess == 0) {
                throw new InternalServerEx("비밀번호 변경에 실패했습니다.");
            }
        }
        // 잘못된 요청
        else {
            throw new BadRequestEx("변경 작업 중 오류가 발생했습니다.");
        }
    }

    public Optional<MemberProfileDTO> findMyProfileById(Long id) {
        Optional<MemberProfileDTO> memberProfile = repository.findMemberProfileById(id);

        return memberProfile;
    }

    public int deleteMember(Long id) {
        int isSuccess = repository.deleteMember(id);

        return isSuccess;
    }
    // public Map<String, String> login(MemberDto memberDto) 에서 호출
    public DuplicateCheckDTO isDuplicateMember(String email, String nickname, String phoNo) {
        // existsByEmail = 1 이면 true, 0(다른값) 이면 false
        boolean emailDuplicate = (repository.existsByEmail(email) == 1);
        boolean nicknameDuplicate = (repository.existsByNickname(nickname) == 1);
        boolean phoNoDuplicate = (repository.existsByPhoNo(phoNo) == 1);

        return new DuplicateCheckDTO(emailDuplicate, nicknameDuplicate, phoNoDuplicate);
    }

    public boolean isNicknameDuplicate(String nickname) {
        // existsByNickname이 1이면 중복(true), 0이면 중복 아님(false)
        return repository.existsByNickname(nickname) == 1;
    }
}


