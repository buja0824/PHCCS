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

    @DeleteMapping("/pet/delete/{id}")
    public ResponseEntity<?> petDelete(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @PathVariable("id") Long deletePetId){

        log.info("petDelete()");
        if(loginMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
        }
        service.deletePet(loginMember.getId(), deletePetId);
        return ResponseEntity.ok("삭제");
    }
    @Data
    static class Member {
        private Long id;
    }

}
