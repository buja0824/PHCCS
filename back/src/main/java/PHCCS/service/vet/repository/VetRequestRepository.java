package PHCCS.service.vet.repository;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.vet.dto.VetRequestDTO;

import java.util.List;

public interface VetRequestRepository {
    int save(VetRequestDTO vetRequestDTO);

    List<VetRequestModel> findAll();

    VetRequestDTO findById(Long id);

    Long findMemberIdById(Long id);

    int deleteById(Long Id);
}