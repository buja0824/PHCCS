package PHCCS.back.web.service;

import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

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
}
