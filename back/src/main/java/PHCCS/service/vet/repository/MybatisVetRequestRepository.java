package PHCCS.service.vet.repository;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.vet.dto.VetRequestDTO;
import PHCCS.service.vet.repository.mapper.VetRequestMapper;
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

        log.info("MybatisVetRequestRepository - save 실행");
        int save = mapper.save(vetRequestDTO);
        log.info("MybatisVetRequestRepository - save 완료");
        return save;
    }

    @Override
    public List<VetRequestModel> findAll(){
        log.info("MybatisVetRequestRepository - findAll 실행");
        return mapper.findAll();
    }

    @Override
    public VetRequestDTO findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public Long findMemberIdById(Long id) {
        return mapper.findMemberIdById(id);
    }

    @Override
    public int deleteById(Long id) {
        return mapper.deleteById(id);
    }

    @Override
    public int existsByMemberIdAndEmailAndLicenseNo(Long Id, String email, String licenseNo) {
        return mapper.existsByMemberIdAndEmailAndLicenseNo(Id, email, licenseNo);
    }

}
