package PHCCS.service.vet.repository.mapper;

import PHCCS.service.vet.dto.VetInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VetInfoMapper {
    int save(VetInfoDTO vetInfoDTO);

    int existsByLicenseNo(@Param("licenseNo") String licenseNo);
}

