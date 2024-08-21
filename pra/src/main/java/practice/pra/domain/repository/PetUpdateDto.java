package practice.pra.domain.repository;

import lombok.Data;

@Data
public class PetUpdateDto {

    private String petName;
    private String petSpecies;
    private String petAge;
}
