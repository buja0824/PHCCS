package practice.pra.domain.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practice.pra.domain.Pet;
import practice.pra.domain.repository.PetUpdateDto;

@Mapper
public interface PetMapper {
    void save(Pet pet);
    void update(@Param("id") Long id, @Param("updateParam") PetUpdateDto updateDto);
}
