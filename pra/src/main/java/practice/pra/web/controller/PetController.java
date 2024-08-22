package practice.pra.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import practice.pra.domain.Member;
import practice.pra.domain.Pet;
import practice.pra.domain.repository.PetRepository;
import practice.pra.web.SessionConst;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PetController {

    private final PetRepository petRepository;
    @ResponseBody
    @GetMapping("/pet/add")
    public Pet savePet(/*@RequestBody Pet pet,*/
                        @RequestParam("name") String name,
                        @RequestParam("age") String age,
                        @RequestParam("spe") String spe,
                        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
        if(loginMember == null){
            log.info("로그인된 사용자가 아님");
            return null;
        }
        log.info("sessionId = {}", loginMember.getId());
        Pet pet = new Pet();
        pet.setPetOwner(loginMember.getId());
        pet.setPetName(name);
        pet.setPetAge(age);
        pet.setPetSpecies(spe);
        log.info("id = {}", pet.getId());
        log.info("petName = {}", pet.getPetName());
        log.info("species = {}", pet.getPetSpecies());
        log.info("owner = {}", pet.getPetOwner());

        petRepository.save(pet);
        return pet;
    }

}
