package PHCCS.service.admin.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VetRequestModel {
    private Long id;
    private String name;
    private String email;
    private String hospitalName;
    private String requestDate;

    public VetRequestModel() {
        this.requestDate = LocalDate.now() + ""; // 기본 생성 시 현재 날짜 설정
    }

    public VetRequestModel(Long id, String name, String email, String hospitalName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hospitalName = hospitalName;
        this.requestDate = LocalDate.now() + "";
    }
}