package PHCCS.back.web.repository;

import PHCCS.back.domain.Pet;

import java.util.Optional;

public interface
PetRepository {

    int save(Pet pet);
}
