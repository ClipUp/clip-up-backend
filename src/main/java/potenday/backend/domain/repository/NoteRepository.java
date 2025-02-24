package potenday.backend.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import potenday.backend.domain.Note;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findAllByIdInAndIsDeleted(List<String> id, Boolean isDeleted);

}
