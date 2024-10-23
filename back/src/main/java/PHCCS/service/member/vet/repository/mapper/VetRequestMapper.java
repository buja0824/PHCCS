package PHCCS.service.member.vet.repository.mapper;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.member.vet.dto.VetRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VetRequestMapper {
    int save(VetRequestDTO vetRequestDTO);

    List<VetRequestModel> findAll();
}
