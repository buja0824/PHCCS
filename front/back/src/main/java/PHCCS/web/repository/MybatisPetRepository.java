package PHCCS.web.repository;

import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetmodifyParam;
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
    public int save(Long id,Pet pet) {
        int save = mapper.save(id, pet);
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
    public void modifyPet(Long memberId, String name, PetmodifyParam modifyParam) {
        log.info("MybatisPetRepository modifyPet()");
        mapper.modifyPet(memberId, name, modifyParam);
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
