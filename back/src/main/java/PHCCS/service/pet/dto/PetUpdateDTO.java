package PHCCS.service.pet.dto;

import lombok.Data;

@Data
public class PetUpdateDTO {

    private String petName;
    private String petAge;
    private String petBreed;
    private String petGender;

}
