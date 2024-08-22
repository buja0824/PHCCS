package practice.pra.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExController { // 이미 존재하는 id로 회원가입을 하면 예외를 발생시킴

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<String> memberAlreadyExistsEx(MemberAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

//    @ExceptionHandler(MemberAlreadyExistsException.class)
//    public ResponseEntity<String> sendNoteEx(IllegalStateException e){
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
//    }
}
