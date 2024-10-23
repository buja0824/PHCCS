package PHCCS.service.member.vet.repository;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.member.vet.dto.VetRequestDTO;
import PHCCS.service.member.vet.repository.mapper.VetRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisVetRequestRepository implements VetRequestRepository {
    final private VetRequestMapper mapper;

    @Override
    public int save(VetRequestDTO vetRequestDTO) {
        int save = mapper.save(vetRequestDTO);
        return save;
    }

    @Override
    public List<VetRequestModel> findAll(){
        return mapper.findAll();
    }
}
