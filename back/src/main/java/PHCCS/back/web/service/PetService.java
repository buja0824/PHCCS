package PHCCS.back.web.service;

import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.PetRepository;
import PHCCS.back.web.repository.domain.PetmodifyParam;
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

    public ResponseEntity<?> save(Pet pet) {
        int resultRow = repository.save(pet);
        if (resultRow < 0) {
            return ResponseEntity.badRequest().body("펫 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok("정상적으로 등록 되었습니다.");
    }

    public List<Pet> findPetsByMember(Long id){
        return repository.findPetsByMember(id);
    }

    public Pet findById(Long id){
        return repository.findById(id);
    }

    public void modifyPet(Long memberId, Long id, PetmodifyParam modifyParam){
        repository.modifyPet(memberId, id, modifyParam);
    }

    public void deletePet(Long memberId, List<Long> petIds){
        if(petIds.isEmpty() || petIds == null){
            throw new IllegalArgumentException("삭제할 데이터가 없습니다.");
        }
        repository.deletePet(memberId, petIds);
    }

}
