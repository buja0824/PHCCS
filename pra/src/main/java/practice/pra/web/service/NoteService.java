package practice.pra.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import practice.pra.domain.Note;
import practice.pra.domain.repository.NoteRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository repository;

    public void sendNote(Note note){
        repository.send(note);
    }

}
