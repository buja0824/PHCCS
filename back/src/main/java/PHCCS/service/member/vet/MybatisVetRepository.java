package PHCCS.service.member.vet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisVetRepository implements VetRepository{
    final private VetMapper mapper;

    @Override
    public int save(VetSignupDTO vetSignupDTO) {
        int save = mapper.save(vetSignupDTO);
        return save;
    }
}
