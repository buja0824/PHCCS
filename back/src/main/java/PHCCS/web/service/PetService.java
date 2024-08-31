package PHCCS.web.service;

import PHCCS.domain.Pet;
import PHCCS.web.repository.PetRepository;
import PHCCS.web.service.domain.PetDto;
import PHCCS.web.repository.domain.PetUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;

    public ResponseEntity<?> save(Long memberId, PetDto dto) {
        Pet pet = new Pet();
        pet.setPetRegNo(dto.getPetRegNo());
        pet.setMemberId(memberId);
        pet.setPetName(dto.getPetName());
        pet.setPetBreed(dto.getPetBreed());
        pet.setPetAge(dto.getPetAge());
        pet.setPetGender(dto.getPetGender());

        int resultRow = repository.save(pet);
        if (resultRow < 0) {
            return ResponseEntity.badRequest().body("펫 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok("정상적으로 등록 되었습니다.");
    }

    public List<Pet> findPetsByMember(Long id){
        return repository.findPetsByMember(id);
    }

    public Pet findByRegNo(String regNo){
        return repository.findByRegNo(regNo);
    }

    public void updatePet(Long memberId, String petName, PetUpdateDto updateParam){
        log.info("service modifyPet()");
        repository.updatePet(memberId, petName, updateParam);
    }

    public void deletePet(Long memberId, List<String> petNames){
        if(petNames.isEmpty() || petNames == null){
            throw new IllegalArgumentException("삭제할 데이터가 없습니다.");
        }
        repository.deletePet(memberId, petNames);
    }

    public void testDelete(){
        repository.testDelete();
    }

}
