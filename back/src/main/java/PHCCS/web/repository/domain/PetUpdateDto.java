package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class PetUpdateDto {

    private String petName;
    private String petAge;
    private String petBreed;
    private String petGender;

}
