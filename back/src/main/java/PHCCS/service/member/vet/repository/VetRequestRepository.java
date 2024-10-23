package PHCCS.service.member.vet.repository;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.member.vet.dto.VetRequestDTO;

import java.util.List;

public interface VetRequestRepository {
    int save(VetRequestDTO vetRequestDTO);

    List<VetRequestModel> findAll();
}
