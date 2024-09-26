package PHCCS.domain;

import lombok.Data;

@Data
public class Pet {

    private String petRegNo;// 반려동물 등록 번호 기본키로 사용
    private Long memberId; // 반려동물 주인의 기본키 : FK로 사용

    private String petName;
    private String petBreed;
    private Long petAge;
    private String petGender;

}
