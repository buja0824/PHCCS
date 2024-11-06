package PHCCS.service.vet.repository;

import PHCCS.service.vet.dto.VetInfoDTO;

public interface VetInfoRepository {
    int save(VetInfoDTO vetInfoDTO);

    int existsByLicenseNo(String licenseNo);
}
