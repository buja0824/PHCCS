package practice.pra.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import practice.pra.domain.Member;
import practice.pra.domain.repository.MemberRepository;
import practice.pra.domain.repository.MemberSearchCon;
import practice.pra.domain.repository.MemberUpdateDto;
import practice.pra.web.service.EmailService;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    @PostMapping("/add")
    public String saveMember(
/*            @RequestParam("name") String name,
            @RequestParam("id") String memberId,
            @RequestParam("password") String password*/
            @RequestBody Member member){

//        log.info("save Member Start");
//        log.info("id = {}", memberId);
//        log.info("name = {}", name);
//        log.info("password = {}", password);

//        Member member = new Member();
//        member.setMemberId(memberId);
//        member.setName(name);
//        member.setPassword(password);
//        Optional<Member> existMember = memberRepository.findByLoginId(memberId);
        Optional<Member> existMember = memberRepository.findByLoginId(member.getLoginId());
        if(!existMember.isEmpty()){
            log.info("존재하는 id");
            throw new MemberAlreadyExistsException("이미 존재하는 ID 입니다." + member.getLoginId());
        }
        log.info("id = {}", member.getId());
        log.info("name = {}", member.getName());
        log.info("memberId = {}", member.getLoginId());
        log.info("password = {}", member.getPassword());
        log.info("getPhoNo = {}", member.getPhoNo());
        log.info("getEmail = {}", member.getEmail());
        log.info("getBirthDate = {}", member.getBirthdate());
        log.info("getNickName = {}", member.getNickName());

        log.info("세이브 한다");
        memberRepository.save(member);
        log.info("세이브 끝");

        return "멤버 저장 완료 이름 : " + member.getName(); // 상태코드 반환
    }

    @GetMapping("/find-id")
    public String findMemberLoginId(@RequestBody MemberSearchCon memberSearchCon){
        log.info("id 찾기");

        String loginIdByNameAndEmail = memberRepository.findLoginIdByNameAndEmail(memberSearchCon.getName(), memberSearchCon.getEmail());
        log.info("loginIdByNameAndEmail = {}", loginIdByNameAndEmail);
        if(loginIdByNameAndEmail == null){
            return "존재하지 않는 회원정보 입니다.";
        }
        emailService.sendSimpleEmail(memberSearchCon.getEmail(), "아이디 찾은 결과 입니다.", "찾은 아이디 입니다." + loginIdByNameAndEmail);
        return loginIdByNameAndEmail;
    }

    @GetMapping("/find-pwd")
    public String findMemberPwd(@RequestBody MemberSearchCon memberSearchCon){
        log.info("password 찾기");

        Optional<Member> findPwdMember = memberRepository.findPwdByIdAndEmail(memberSearchCon.getLoginId(), memberSearchCon.getEmail());
        log.info("findPwdMember = {}", findPwdMember);
        if(findPwdMember.isEmpty()){
            return "존재하지 않는 회원정보 입니다.";
        }
        log.info("password = {}", findPwdMember.get().getPassword());
        emailService.sendSimpleEmail(memberSearchCon.getEmail(), "비밀번호 변경", "member/update/"+findPwdMember.get().getId());
        return "비밀번호 찾기 이메일 발송 완료";
    }

    @PostMapping("/update/{id}")
    public String  updateMember(
            @PathVariable("id") Long id,
            @RequestBody MemberUpdateDto member){ // json데이터 받는거

        log.info("save update Start");

        log.info("id = {}", id);
        log.info("name = {}", member.getName());
        log.info("password = {}", member.getPassword());
        log.info("getPhoNo = {}", member.getPhoNo());
        log.info("getEmail = {}", member.getEmail());
        log.info("getNickName = {}", member.getNickName());
        Member updateMember = memberRepository.findById(id).get();
        log.info("id = {}", id);
        log.info("updatename = {}", updateMember.getName());
        log.info("updatepassword = {}", updateMember.getPassword());
        log.info("updategetPhoNo = {}", updateMember.getPhoNo());
        log.info("updategetEmail = {}", updateMember.getEmail());
        log.info("updategetNickName = {}", updateMember.getNickName());
        if(member.getName() == null){
            member.setName(updateMember.getName());
        }
        if(member.getPassword() == null){
            member.setPassword(updateMember.getPassword());
        }
        if(member.getPhoNo() == null){
            member.setPhoNo(updateMember.getPhoNo());
        }
        if(member.getEmail() == null){
            member.setEmail(updateMember.getEmail());
        }
        if(member.getNickName() == null){
            member.setNickName(updateMember.getNickName());
        }

        log.info("id = {}", id);
        log.info("name = {}", member.getName());
        log.info("password = {}", member.getPassword());
        log.info("getPhoNo = {}", member.getPhoNo());
        log.info("getEmail = {}", member.getEmail());
        log.info("getNickName = {}", member.getNickName());
//        MemberUpdateDto updateMember = new MemberUpdateDto();
//        updateMember.setName(member.getName());
//        updateMember.setPassword(member.getPassword());
        log.info("수정 한다");
        memberRepository.update(id,member);
        log.info("수정 끝");
        return member.getName() + " " +
                member.getPassword() + " " +
                member.getEmail() +" "+
                member.getNickName() +" "+
                member.getPhoNo();
    }
}

class MemberAlreadyExistsException extends RuntimeException {
    public MemberAlreadyExistsException() {
    }

    public MemberAlreadyExistsException(String message) {
        super(message);
    }
}
