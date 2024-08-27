package PHCCS.domain;

import lombok.Data;

@Data
public class Pet {

//    private Long id; // 펫 기본키?
    private String petRegNo;// 반려동물 등록 번호 기본키로 사용

    private String petName;
    private String petBreed;
    private String petAge;
    private String petGender;


}
