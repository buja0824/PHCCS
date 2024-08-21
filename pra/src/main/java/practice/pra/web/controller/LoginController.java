package practice.pra.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import practice.pra.domain.Member;
import practice.pra.web.LoginForm;
import practice.pra.web.SessionConst;
import practice.pra.web.service.LoginService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    @ResponseBody
    @GetMapping("/login")
    public String login( //주석 처리 내용은 json으로 받을 때
            /*@Valid @RequestBody LoginForm form*/
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password,
//            BindingResult result,
            HttpServletRequest request) {
        log.info("login 접속");
//        if(result.hasErrors()){
//            return "오류 발생";
//        }
        Member loginMember = loginService.findLoginMember(loginId);
//        Member loginMember = loginService.findLoginMember(loginMember.getLoginId());
        log.info("login Member? {}", loginMember);

//        if(loginMember == null){
//            result.reject("아이디 또는 비밀번호가 올바르지 않습니다.");
//            return "redirect:/";
//        }
        HttpSession session = request.getSession();// 세션이 없으면 생성 있으면 있는거 반환
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);



        return "로그인 완료 " + session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        // 세션 삭제
        HttpSession session = request.getSession(false);
        if(session!=null){
            session.invalidate();// 세션 파괴
        }
        log.info("로그아웃 완료");
        return "redirect:/";
    }


}
