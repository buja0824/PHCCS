package PHCCS.back.web.repository.mapper;

import PHCCS.back.domain.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {
    int save(Pet pet);

    List<Pet> findPetsByMember(Long id);

    void deletePet(@Param("petId") Long petId, @Param("memberId") Long memberId);
}
