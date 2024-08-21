package PHCCS.back.web.controller;


import PHCCS.back.domain.Pet;
import PHCCS.back.web.repository.PetRepository;
import PHCCS.back.web.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PetController {

    private final PetService service;
    @PostMapping("/pet/add")
    public ResponseEntity<?> petAdd(@RequestBody Pet pet){
        log.info("petAdd()");
        ResponseEntity<?> save = service.save(pet);
        return save;
    }

}
