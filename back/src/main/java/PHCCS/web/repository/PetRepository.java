package PHCCS.web.repository;

import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetmodifyParam;

import java.util.List;

public interface PetRepository {

    int save(Pet pet);

    Pet findById(Long id);

    List<Pet> findPetsByMember(Long id);

    void modifyPet(Long memberId, Long id, PetmodifyParam modifyParam);

    void deletePet(Long memberId, List<Long> petIds);
}
