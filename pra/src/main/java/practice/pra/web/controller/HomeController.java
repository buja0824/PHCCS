package practice.pra.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import practice.pra.domain.Member;
import practice.pra.web.SessionConst;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @ResponseBody
    @GetMapping("/")
    public String home(){
        log.info("로그인 안된 멤버의 홈");
        return "로그인 안된 사람의 홈 접속 완료";
    }

    @ResponseBody
    @GetMapping("/home")
    public String loginHome(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember){
        log.info("home 접속");

        if(loginMember == null){
            log.info("세션에 회원 데이터가 없음");

            return "redirect:/";
        }
        log.info("로그인 했군요");

        return "홈 접속 완료 " + loginMember;
    }

}
