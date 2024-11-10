package PHCCS.service.vet.repository.mapper;

import PHCCS.service.admin.model.VetRequestModel;
import PHCCS.service.vet.dto.VetRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VetRequestMapper {
    int save(VetRequestDTO vetRequestDTO);

    List<VetRequestModel> findAll();

    VetRequestDTO findById(Long id);

    Long findMemberIdById(Long id);

    int deleteById(Long id);
}
