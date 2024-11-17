package PHCCS.service.pet.dto;

import lombok.Data;

@Data
public class PetUpdateDTO {

    private String petName;
    private Long petAge;
    private String petBreed;
    private String petGender;

}
