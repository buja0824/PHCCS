package PHCCS.web.repository;

import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetmodifyParam;
import PHCCS.web.repository.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public Pet findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public List<Pet> findPetsByMember(Long id) {
        return mapper.findPetsByMember(id);
    }

    @Override
    public void modifyPet(Long memberId, Long id, PetmodifyParam modifyParam) {
        mapper.modifyPet(memberId, id, modifyParam);
    }

    @Override
    public void deletePet(Long memberId, List<Long> petIds ) {
        mapper.deletePet(memberId,petIds);
    }
}
