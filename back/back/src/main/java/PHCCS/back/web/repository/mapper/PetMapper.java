package PHCCS.back.web.repository.mapper;

import PHCCS.back.domain.Pet;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface PetMapper {
    int save(Pet pet);


}
