package PHCCS.web.controller;


import PHCCS.SessionConst;
import PHCCS.domain.Member;
import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetmodifyParam;
import PHCCS.web.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @PostMapping("/pet/add")
    public ResponseEntity<?> petAdd(
            /*@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,*/
            @RequestBody Pet pet){
        log.info("petAdd()");
        ResponseEntity<?> save = service.save(/*loginMember.getId()*/1L,pet);
        return save;
    }

    @GetMapping("/pet/showAll")
    public ResponseEntity<?> showMyPet(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember){
        log.info("showMyPet()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
//        }
        List<Pet> pets = service.findPetsByMember(/*loginMember.getId()*/ 1L);
        log.info(pets.toString());
        return ResponseEntity.ok(pets);
    }

    @DeleteMapping("/pet/delete")
    public ResponseEntity<?> petDelete(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestBody List<String> petNames){

        log.info("petDelete()");

//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
//        }
        try{
            service.deletePet(/*loginMember.getId()*/1L, petNames);
            return ResponseEntity.ok("삭제 완료");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("반려동물을 선택해주세요");
        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류가 발생하였습니다.");
//        }
    }

    @PutMapping("/pet/modify/{name}")
    public ResponseEntity<?> modifyPet(
            /*@SessionAttribute(name = SessionConst.LOGIN_MEMBER,required = false) Member loginMember,*/
            @PathVariable("name") String petName,
            @RequestBody PetmodifyParam modifyParam){

        log.info("modifyPet()");
//        if(!isLogin(loginMember)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
//        }

        service.modifyPet(/*loginMember.getId()*/1L, petName, modifyParam);

        return ResponseEntity.ok("수정 완료");
    }

    public static boolean isLogin(Member loginMember){
        if(loginMember == null) return false;
        else return true;
    }


}
