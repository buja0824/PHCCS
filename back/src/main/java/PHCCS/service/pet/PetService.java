package PHCCS.service.pet;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.service.pet.dto.PetDTO;
import PHCCS.service.pet.dto.PetUpdateDTO;
import PHCCS.service.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;

    public void save(Long memberId, PetDTO dto) {
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
    }

    public List<PetDTO> findPetsByMember(Long id){
        return repository.findPetsByMember(id);
    }

    public Pet findByRegNo(String regNo){
        return repository.findByRegNo(regNo);
    }

    public void updatePet(Long memberId, String petName, PetUpdateDTO updateParam){
        log.info("service modifyPet()");
        repository.updatePet(memberId, petName, updateParam);
    }

    public void deletePet(Long memberId, String name){
        if(name.isEmpty()){
            throw new BadRequestEx("반려동물을 선택해주세요.");
        }
//        name.forEach(it -> {
//            Pet pet = repository.findByRegNo(it);
//            if(pet==null) throw new BadRequestEx("반려동물이 존재하지 않습니다.");
//        });
        repository.deletePet(memberId, name);
    }

    public void testDelete(){
        repository.testDelete();
    }

}
