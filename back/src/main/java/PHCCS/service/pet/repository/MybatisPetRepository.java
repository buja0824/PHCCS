package PHCCS.service.pet.repository;

import PHCCS.service.pet.Pet;
import PHCCS.service.pet.dto.PetDTO;
import PHCCS.service.pet.dto.PetUpdateDTO;
import PHCCS.service.pet.repository.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisPetRepository implements PetRepository {

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
    public List<PetDTO> findPetsByMember(Long id) {
        return mapper.findPetsByMember(id);
    }

    @Override
    public void updatePet(Long memberId, String name, PetUpdateDTO updateDto) {
        log.info("MybatisPetRepository modifyPet()");
        mapper.updatePet(memberId, name, updateDto);
    }

    @Override
    public int deletePet(Long memberId, String name ) {
        return mapper.deletePet(memberId, name);
    }

    @Override
    public void testDelete() {
        mapper.testDelete();
    }
}
