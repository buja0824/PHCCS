package PHCCS.service.vet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
public class VetInfoDTO {
    private Long id;
    private Long memberId;
    private String licenseNo;
    private String licenseProfile;
    private String HospitalName;
    private String hospitalAddr;
}
