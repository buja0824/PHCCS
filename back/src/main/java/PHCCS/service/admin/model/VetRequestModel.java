package PHCCS.service.admin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class VetRequestModel {
    private Long id;
    private Long memberId;
    private String licenseNo;
    private String email;
    private String name;
    private String hospitalName;
    private String hospitalAddr;
    private LocalDate requestDate;

}