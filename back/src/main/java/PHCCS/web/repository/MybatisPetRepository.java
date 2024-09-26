package PHCCS.web.repository;

import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetUpdateDTO;
import PHCCS.web.repository.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
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
    public Pet findByRegNo(String regNo) {
        return mapper.findByRegNo(regNo);
    }

    @Override
    public List<Pet> findPetsByMember(Long id) {
        return mapper.findPetsByMember(id);
    }

    @Override
    public void updatePet(Long memberId, String name, PetUpdateDTO updateDto) {
        log.info("MybatisPetRepository modifyPet()");
        mapper.updatePet(memberId, name, updateDto);
    }

    @Override
    public void deletePet(Long memberId, List<String> petNames ) {
        mapper.deletePet(memberId, petNames);
    }

    @Override
    public void testDelete() {
        mapper.testDelete();
    }
}
