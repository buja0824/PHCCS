package PHCCS.service.member.vet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class VetRequestDTO {
    private Long id;
    private String licenseNo;
    private String email;
    private String name;
    private String hospitalName;
    private String hospitalAddr;
    private LocalDate requestDate;
}
