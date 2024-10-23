package PHCCS.service.member.vet.repository.mapper;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.member.vet.dto.VetRequestDTO;

import java.util.List;

public interface VetRequestMapper {
    int save(VetRequestDTO vetRequestDTO);

    List<VetRequestModel> findAll();
}
