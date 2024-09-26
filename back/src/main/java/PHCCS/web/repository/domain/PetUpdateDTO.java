package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class PetUpdateDTO {

    private String petName;
    private String petAge;
    private String petBreed;
    private String petGender;

}
