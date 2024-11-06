package PHCCS.service.vet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
public class VetRequestDTO {
    private Long id;
    private Long MemberId;
    private String licenseNo;
    private String email;
    private String name;
    private String hospitalName;
    private String hospitalAddr;
    private LocalDate requestDate;
}
