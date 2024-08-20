package practice.pra.domain;

import lombok.Data;


@Data
public class Pet {

    private Long id;

    private String petName;
    private String petAge;
    private String petSpecies;
    private Long petOwner;
}
