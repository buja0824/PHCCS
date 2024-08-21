package practice.pra.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import practice.pra.domain.Member;
import practice.pra.domain.Note;
import practice.pra.web.SessionConst;
import practice.pra.web.service.NoteService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService service;
//    @GetMapping("")
//    public List<Note> showNotes(){
//
//
//        return
//    }
    @PostMapping("/send") //쪽지를 전송 하려면 게시글 id 해당 게시글 작성자 id 필요
    public void sendNote(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestBody Note note
    ){
        //  쪽지를 받는 사람과 보내는 사람이 존재하고 게시판별로 하지 않고 사람별로 한다
//        if(loginMember == null || loginMember.getId() != note.getSenderId()) return;
        log.info("1");
        note.setReadChk(1L); // 안읽음
        log.info("2");
        service.sendNote(note); // 쪽지방, 보내는 사람, 받는 사람, 내용
    }


}
