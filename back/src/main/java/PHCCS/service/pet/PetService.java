package PHCCS.service.pet;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.service.pet.dto.PetDTO;
import PHCCS.service.pet.dto.PetUpdateDTO;
import PHCCS.service.pet.repository.PetRepository;
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

    public ResponseEntity<?> save(Long memberId, PetDTO dto) {
        Pet pet = new Pet();
        pet.setPetRegNo(dto.getPetRegNo());
        pet.setMemberId(memberId);
        pet.setPetName(dto.getPetName());
        pet.setPetBreed(dto.getPetBreed());
        pet.setPetAge(dto.getPetAge());
        pet.setPetGender(dto.getPetGender());

        int resultRow = repository.save(pet);
        if (resultRow < 0) {
            throw new BadRequestEx("반려동물 등록에 실패 했습니다.");

        }
        return ResponseEntity.ok("정상적으로 등록 되었습니다.");
    }

    public List<Pet> findPetsByMember(Long id){
        return repository.findPetsByMember(id);
    }

    public Pet findByRegNo(String regNo){
        return repository.findByRegNo(regNo);
    }

    public void updatePet(Long memberId, String petName, PetUpdateDTO updateParam){
        log.info("service modifyPet()");
        repository.updatePet(memberId, petName, updateParam);
    }

    public int deletePet(Long memberId, List<String> petNames){
        if(petNames.isEmpty() || petNames == null){
            throw new BadRequestEx("삭제할 데이터가 없습니다.");
        }
        return repository.deletePet(memberId, petNames);
    }

    public void testDelete(){
        repository.testDelete();
    }

}
