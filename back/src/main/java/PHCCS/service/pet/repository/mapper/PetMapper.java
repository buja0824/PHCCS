package PHCCS.service.pet.repository.mapper;

import PHCCS.service.pet.Pet;
import PHCCS.service.pet.dto.PetUpdateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {
    int save(@Param("pet") Pet pet);

    List<Pet> findPetsByMember(Long id);

    Pet findByRegNo(String regNo);

    void updatePet(@Param("memberId") Long memberId, @Param("name") String name, @Param("updateDto") PetUpdateDTO updateDto);

    void deletePet(@Param("memberId") Long memberId, @Param("petNames") List<String> petNames);

    void testDelete();
}
