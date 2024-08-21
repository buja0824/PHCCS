package PHCCS.back.web.controller;

import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.PetRepository;
import PHCCS.back.web.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PetControllerTest {
    private final PetRepository repository;
    private final PetService service;
    public PetControllerTest(PetRepository repository, PetService service) {
        this.repository = repository;
        this.service = service;
    }

    @Test
    public void showPetTest(){
        List<Pet> petsByMember = service.findPetsByMember(2L);
        System.out.println(petsByMember);
    }

}
