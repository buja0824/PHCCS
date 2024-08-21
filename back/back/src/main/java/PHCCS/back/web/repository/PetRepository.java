package PHCCS.back.web.repository;

import PHCCS.back.domain.Pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository {

    int save(Pet pet);

    List<Pet> findPetsByMember(Long id);

    void deletePet(Long petId, Long memberId);
}
