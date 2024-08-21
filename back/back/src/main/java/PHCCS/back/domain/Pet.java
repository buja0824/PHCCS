package PHCCS.back.domain;

import lombok.Data;

@Data
public class Pet {

    private Long id; // 펫 기본키?

    private String name;
    private String species;
    private String age;
    private String gender;
    private String regNo;// 반려동물 등록 번호

    public Pet() {
    }

    public Pet(Long id, String name, String species, String age, String gender, String regNo) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.gender = gender;
        this.regNo = regNo;
    }
}
