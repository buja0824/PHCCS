package PHCCS.service.member;

import PHCCS.common.jwt.JwtUtil;
import PHCCS.service.member.dto.DuplicateCheckDto;
import PHCCS.service.member.dto.MemberDTO;
import PHCCS.service.member.dto.MemberModifyDTO;
import PHCCS.service.member.dto.MemberProfileDTO;
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
            throw new RuntimeException("회원을 찾을 수 없음.");
        }

        Member member = optionalMember.get();

        if(!member.getPwd().equals(memberDto.getPwd())){
            throw new RuntimeException("검증 되지 않음.");
        }

        Map<String, String> tokens = new HashMap<>();
        String newAccessToken = jwtUtil.createAccessToken(member.getId(), member.getRole());
        tokens.put("accessToken", newAccessToken);
        // log.info("accestokenId: {}", jwtUtil.extractId(newAccessToken));
        // log.info("accestokenrole: {}", jwtUtil.extractRole(newAccessToken));
        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        tokenService.storeRefreshToken(jwtUtil.extractId(refreshToken), jwtUtil.actual(refreshToken));
        tokens.put("refreshToken", refreshToken);

        // [LOG] 토큰 발급 확인
        log.info("보내는 토큰: {}", tokens);

        return tokens;
    }

    public boolean logout(String token){
        return tokenService.removeRefreshToken(jwtUtil.extractId(token), jwtUtil.actual(token));
    }

    public int modifyMember (Long id, MemberModifyDTO memberModifyDto){
        int isSuccess = repository.modifyMember(id, memberModifyDto);

        return isSuccess;
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
    public DuplicateCheckDto isDuplicateMember(String email, String nickname, String phoNo) {
        // existsByEmail = 1 이면 true, 0(다른값) 이면 false
        boolean emailDuplicate = (repository.existsByEmail(email) == 1);
        boolean nicknameDuplicate = (repository.existsByNickname(nickname) == 1);
        boolean phoNoDuplicate = (repository.existsByPhoNo(phoNo) == 1);

        return new DuplicateCheckDto(emailDuplicate, nicknameDuplicate, phoNoDuplicate);
    }
}


