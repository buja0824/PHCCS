package PHCCS.back.web.controller;

import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PetControllerTest {
    private final PetRepository repository;

    public PetControllerTest(PetRepository repository) {
        this.repository = repository;
    }

    @Test
    public void petSaveTest(){}
    // given
    Pet pet = new Pet(1L, "똘", "푸들", "1", "남자", "1215");

    // when


    // then
}
