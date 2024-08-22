package PHCCS.web.controller;

import PHCCS.SessionConst;
import PHCCS.domain.Member;
import PHCCS.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    @PostMapping("/post")
    public ResponseEntity<?> createPost(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestBody Post post){
        log.info("createPost()");
        if(!isLogin(loginMember)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
        }
        //TODO


        return null;
    }
    public static boolean isLogin(Member loginMember){
        if(loginMember == null) return false;
        else return true;
    }

}
