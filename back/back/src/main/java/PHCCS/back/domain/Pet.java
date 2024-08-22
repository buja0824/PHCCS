package PHCCS.back.domain;

import lombok.Data;

@Data
public class Pet {

    private Long id; // 펫 기본키?

    private String petName;
    private String petBreed;
    private String petAge;
    private String petGender;
    private String petRegNo;// 반려동물 등록 번호


}
