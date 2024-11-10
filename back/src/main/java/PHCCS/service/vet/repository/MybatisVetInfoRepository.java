package PHCCS.service.vet.repository;

import PHCCS.service.vet.dto.VetInfoDTO;
import PHCCS.service.vet.repository.mapper.VetInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisVetInfoRepository implements VetInfoRepository {

    final private VetInfoMapper mapper;

    @Override
    public int save(VetInfoDTO vetInfoDTO) {
        int save = mapper.save(vetInfoDTO);

        return save;
    }

    @Override
    public int existsByLicenseNo(String licenseNo) {
        return mapper.existsByLicenseNo(licenseNo);
    }
}
