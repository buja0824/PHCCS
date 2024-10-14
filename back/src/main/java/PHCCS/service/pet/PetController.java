package PHCCS.service.pet;


import PHCCS.service.member.Member;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.service.pet.dto.PetDTO;
import PHCCS.service.pet.dto.PetUpdateDTO;
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

    private final JwtUtil jwtUtil;
    private final PetService service;

    @PostMapping("/pet/add")
    public ResponseEntity<?> petAdd(
            @RequestHeader("Authorization") String token,
            @RequestBody PetDTO dto){
        log.info("petAdd()");
        Long memberId = jwtUtil.extractSubject(token);
        ResponseEntity<?> save = service.save(memberId,dto);
        return save;
    }

    @GetMapping("/pet/showAll")
    public ResponseEntity<?> showMyPet(
            @RequestHeader("Authorization") String token){
        log.info("showMyPet()");

        Long memberId = jwtUtil.extractSubject(token);
        List<Pet> pets = service.findPetsByMember(memberId);
        log.info(pets.toString());
        return ResponseEntity.ok(pets);
    }

    @DeleteMapping("/pet/delete")
    public ResponseEntity<?> petDelete(
            @RequestHeader("Authorization") String token,
            @RequestBody List<String> petNames){

        log.info("petDelete()");
        Long memberId = jwtUtil.extractSubject(token);
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
//        }
        try{
            service.deletePet(memberId, petNames);
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
            @RequestHeader("Authorization") String token,
            @PathVariable("name") String petName,
            @RequestBody PetUpdateDTO modifyParam){

        log.info("modifyPet()");
        Long memberId = jwtUtil.extractSubject(token);
//        if(!isLogin(loginMember)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요 합니다.");
//        }

        service.updatePet(memberId, petName, modifyParam);

        return ResponseEntity.ok("수정 완료");
    }

    public static boolean isLogin(Member loginMember){
        if(loginMember == null) return false;
        else return true;
    }


}
