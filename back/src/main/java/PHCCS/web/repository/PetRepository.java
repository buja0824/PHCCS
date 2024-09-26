package PHCCS.web.repository;

import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetUpdateDTO;

import java.util.List;

public interface PetRepository {

    int save(Pet pet);

    Pet findByRegNo(String regNo);

    List<Pet> findPetsByMember(Long id);

    void updatePet(Long memberId, String name, PetUpdateDTO updateDto);
    void deletePet(Long memberId, List<String> petNames);
    void testDelete();
}
