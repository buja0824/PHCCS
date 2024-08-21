package practice.pra.domain.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import practice.pra.domain.Note;

import java.util.List;

@Mapper
public interface NoteMapper {

    void send(Note note);

    List<Note> showAllNotes();
}
