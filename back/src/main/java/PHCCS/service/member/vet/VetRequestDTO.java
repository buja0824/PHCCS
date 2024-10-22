package PHCCS.service.member.vet;

import lombok.Data;

@Data
public class VetRequestDTO {
    private Long id;
    private String name;
    private String email;
    private String hospitalName;
    private String requestDate;
}
