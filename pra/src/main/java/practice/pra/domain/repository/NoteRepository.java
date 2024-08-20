package practice.pra.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.pra.domain.Note;
import practice.pra.domain.repository.mapper.NoteMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoteRepository {

    private final NoteMapper mapper;

    public void send(Note note){
        mapper.send(note);
    }

    public List<Note> showAllNotes(){
        List<Note> notes = mapper.showAllNotes();
        return notes;
    }
}
