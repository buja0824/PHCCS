package PHCCS.service.pet.repository;

import PHCCS.service.pet.Pet;
import PHCCS.service.pet.dto.PetDTO;
import PHCCS.service.pet.dto.PetUpdateDTO;

import java.util.List;

public interface PetRepository {

    int save(Pet pet);

    Pet findByRegNo(String regNo);

    List<PetDTO> findPetsByMember(Long id);

    void updatePet(Long memberId, String name, PetUpdateDTO updateDto);
    int deletePet(Long memberId, List<String> regNo);
    void testDelete();
}
