package PHCCS.web.service;

import PHCCS.domain.Member;
import PHCCS.jwt.JwtUtil;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.repository.domain.MemberModifyDTO;
import PHCCS.web.service.domain.MemberDTO;
import PHCCS.web.service.domain.SessionMemberDTO;
import PHCCS.web.service.domain.DuplicateCheckDto;
import PHCCS.web.service.domain.MemberProfileDTO;
import PHCCS.web.repository.domain.MemberModifyDTO;
import PHCCS.web.service.domain.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.Instant;
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

        int resultRow = repository.save(member);

        if (resultRow == 1) {
            return true;
        }
        return false;
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
        tokens.put("accessToken", jwtUtil.createAccessToken(member.getId(), member.getRole()));

        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        tokenService.storeRefreshToken(refreshToken);

        tokens.put("refreshToken", refreshToken);

        // [LOG] 토큰 발급 확인
        log.info("tokens: {}", tokens);

        return tokens;
    }

    public boolean logout(String token){
        return tokenService.removeRefreshToken(token);
    }

    public int modifyMember (Long id, MemberModifyDTO memberModifyDto){
        int isSuccess = repository.modifyMember(id, memberModifyDto);

        return isSuccess;
    }

    public Optional<MemberProfileDTO> findMyProfileById(Long id) {
        Optional<MemberProfileDTO> memberProfile = repository.findMemberById(id);

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

