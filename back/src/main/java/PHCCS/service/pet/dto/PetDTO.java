package PHCCS.service.pet.dto;

import lombok.Data;

@Data
public class PetDTO {

    private String petRegNo;// 반려동물 등록 번호 기본키로 사용
    private String petName;
    private String petBreed;
    private Long petAge;
    private String petGender;
}
