package PHCCS.memberservice.web.controller;

import PHCCS.memberservice.domain.Member;
import PHCCS.memberservice.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PostMapping("/member/add")
    public ResponseEntity<?> petAdd(@RequestBody Member member){
        log.info("MemberAdd()");
        ResponseEntity<?> save = service.save(member);
        return save;
    }
}
