package PHCCS.back.web.repository;

import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.domain.PetmodifyParam;

import java.util.List;
import java.util.Optional;

public interface PetRepository {

    int save(Pet pet);

    Pet findById(Long id);

    List<Pet> findPetsByMember(Long id);

    void modifyPet(Long memberId, Long id, PetmodifyParam modifyParam);

    void deletePet(Long memberId, List<Long> petIds);
}
