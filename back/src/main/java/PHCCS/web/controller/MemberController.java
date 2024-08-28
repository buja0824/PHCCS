package PHCCS.web.controller;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.MemberDto;
import PHCCS.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> add(@RequestBody Member member) {
        ResponseEntity<?> save = service.save(member);
        return save;
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto) {
        ResponseEntity<?> login = service.login(memberDto);
        return login;
    }
}
