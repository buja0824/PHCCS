package PHCCS.back.web.repository;

import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisPetRepository implements PetRepository{

    private final PetMapper mapper;

    @Override
    public int save(Pet pet) {
        int save = mapper.save(pet);
        return save;
    }

    @Override
    public List<Pet> findPetsByMember(Long id) {
        return mapper.findPetsByMember(id);
    }

    @Override
    public void deletePet(Long petId, Long memberId) {
        mapper.deletePet(petId,memberId);
    }
}
