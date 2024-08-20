package practice.pra.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.pra.web.service.EmailService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    @GetMapping("/email")
    public String sendEMail(){
        log.info("이메일 전송 해보기");

        emailService.sendSimpleEmail("dncl7646@naver.com", "스프링으로 보내는 이메일", "이 메일은 정우철 집에서 시작하여 미국으로 도착하여 트럼프의 저격범을 퇴치하였습니다.");
        return "이메일 전송 성공";
    }
}
