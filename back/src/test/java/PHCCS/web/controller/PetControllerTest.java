package PHCCS.web.controller;

import PHCCS.domain.Pet;
import PHCCS.web.service.domain.PetDto;
import PHCCS.web.repository.domain.PetUpdateDto;
import PHCCS.web.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
class PetControllerTest {

    @Autowired
    private PetService service;


    @BeforeEach
    public void cleanDB() {
        service.testDelete();
    }

    @AfterEach
    public void cleanDBAfterTest() {
        service.testDelete();
    }

    @Test
    public void petAddTest(){
        PetDto pet = new PetDto();

        pet.setPetRegNo("01");

        pet.setPetName("바둑이");
        pet.setPetAge("4");
        pet.setPetBreed("푸들");
        pet.setPetGender("0");

        service.save(1L,pet);

        Pet findPet = service.findByRegNo("01");
        Assertions.assertThat(findPet).isEqualTo(pet);
    }

    @Test
    public void showMyPetTest(){
        PetDto pet1 = new PetDto();
        pet1.setPetRegNo("01");
        pet1.setPetName("바둑이");
        pet1.setPetAge("4");
        pet1.setPetBreed("푸들");
        pet1.setPetGender("0");
        service.save(1L,pet1);

        PetDto pet2 = new PetDto();
        pet2.setPetRegNo("02");
        pet2.setPetName("겨울이");
        pet2.setPetAge("3");
        pet2.setPetBreed("웰시코기");
        pet2.setPetGender("0");
        service.save(1L,pet2);

        PetDto pet3 = new PetDto();
        pet3.setPetRegNo("03");
        pet3.setPetName("여름이");
        pet3.setPetAge("2");
        pet3.setPetBreed("치와와");
        pet3.setPetGender("0");
        service.save(1L,pet3);

        List<Pet> showAllPet = service.findPetsByMember(1L);
        log.info(showAllPet.toString());
        Assertions.assertThat(showAllPet).hasSize(3);
    }

    @Test
    public void deletePetTest(){
        PetDto pet = new PetDto();
        pet.setPetRegNo("01");
        pet.setPetName("바둑이");
        pet.setPetAge("4");
        pet.setPetBreed("푸들");
        pet.setPetGender("0");

        service.save(1L,pet);
        PetDto pet2 = new PetDto();
        pet2.setPetRegNo("02");
        pet2.setPetName("겨울이");
        pet2.setPetAge("3");
        pet2.setPetBreed("웰시코기");
        pet2.setPetGender("0");
        service.save(1L,pet2);

        PetDto pet3 = new PetDto();
        pet3.setPetRegNo("03");
        pet3.setPetName("여름이");
        pet3.setPetAge("2");
        pet3.setPetBreed("치와와");
        pet3.setPetGender("0");

        service.save(1L,pet3);

        List<String> deleteList = new ArrayList<>();
        deleteList.add("바둑이");
        deleteList.add("여름이");
        service.deletePet(1L, deleteList);

        List<Pet> showAllPet = service.findPetsByMember(1L);
        Assertions.assertThat(showAllPet).hasSize(1);
    }
    @Test
    public void petModifyTest(){

        PetDto pet = new PetDto();
        pet.setPetRegNo("01");
        pet.setPetName("바둑이");
        pet.setPetAge("4");
        pet.setPetBreed("푸들");
        pet.setPetGender("0");

        service.save(1L,pet);

        PetDto pet2 = new PetDto();
        pet2.setPetRegNo("02");
        pet2.setPetName("나비");
        pet2.setPetAge("3");
        pet2.setPetBreed("웰시코기");
        pet2.setPetGender("0");
        service.save(1L,pet2);
        log.info(service.findByRegNo("02").toString());

        PetUpdateDto modifyParam = new PetUpdateDto();
        modifyParam.setPetName("여름이");
        modifyParam.setPetAge("3");
        modifyParam.setPetBreed("웰시코기");
        modifyParam.setPetGender("0");

        service.updatePet(1L, "나비", modifyParam);
        log.info(service.findByRegNo("02").toString());
    }

}
