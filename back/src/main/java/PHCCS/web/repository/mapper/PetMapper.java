package PHCCS.web.repository.mapper;

import PHCCS.domain.Pet;
import PHCCS.web.repository.domain.PetmodifyParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {
    int save(Pet pet);

    List<Pet> findPetsByMember(Long id);

    Pet findById(Long id);

    void modifyPet(@Param("memberId") Long memberId, @Param("id") Long id, @Param("modifyParam") PetmodifyParam modifyParam);

    void deletePet(@Param("memberId") Long memberId, @Param("petIds") List<Long> petIds);
}
