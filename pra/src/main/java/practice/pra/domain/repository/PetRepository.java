package practice.pra.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import practice.pra.domain.Member;
import practice.pra.domain.Pet;
import practice.pra.domain.repository.mapper.MemberMapper;
import practice.pra.domain.repository.mapper.PetMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PetRepository {
    private final PetMapper mapper;

    public void save(Pet pet){
        mapper.save(pet);
    }

    public void update(@Param("id")Long id, @Param("updateDto") PetUpdateDto petUpdateParam){
        mapper.update(id, petUpdateParam);
    }

}
