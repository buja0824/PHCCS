package PHCCS.service.member.vet;

import PHCCS.service.admin.model.VetRequestModel;

public interface VetRepository {
    int save(VetRequestDTO vetRequestDTO);
}
