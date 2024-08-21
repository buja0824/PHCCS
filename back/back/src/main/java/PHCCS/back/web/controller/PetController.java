package PHCCS.back.web.controller;


import PHCCS.back.SessionConst;
import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.PetRepository;
import PHCCS.back.web.service.PetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @PostMapping("/pet/add")
    public ResponseEntity<?> petAdd(@RequestBody Pet pet){
        log.info("petAdd()");
        ResponseEntity<?> save = service.save(pet);
        return save;
    }

    @GetMapping("/pet/showAll")
    public ResponseEntity<?> showMyPet(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember){
        log.info("showMyPet()");
        if(loginMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
        }
        List<Pet> pets = service.findPetsByMember(/*loginMember.getId()*/ 2L);
        log.info(pets.toString());
        return ResponseEntity.ok(pets);
    }

    @DeleteMapping("/pet/delete")
    public ResponseEntity<?> petDelete(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestBody List<Long> petIds){

        log.info("petDelete()");

        if(loginMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
        }
        try{
            service.deletePet(loginMember.getId(), petIds);
            return ResponseEntity.ok("삭제 완료");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("반려동물을 선택해주세요");
        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류가 발생하였습니다.");
//        }
    }

    @Data
    static class Member {
        private Long id;
    }

}
