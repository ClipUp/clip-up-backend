package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.domain.Note;
import potenday.backend.domain.repository.NoteRepository;
import potenday.backend.support.ErrorCode;

@RequiredArgsConstructor
@Component
class NoteReader {

    private final NoteRepository noteRepository;

    Note read(String id) {
        return noteRepository.findById(id).orElseThrow(ErrorCode.NOTE_NOT_FOUNDED::toException);
    }

}
