package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.domain.Note;
import potenday.backend.domain.repository.NoteRepository;
import potenday.backend.support.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
class NoteWriter {

    private final IdProvider idProvider;
    private final ClockProvider clockProvider;
    private final FileUploader fileUploader;
    private final STTConverter sttConverter;
    private final ChatClient chatClient;
    private final NoteRepository noteRepository;

    Note create() {
        Note newNote = Note.create(idProvider.nextId(), clockProvider.millis());
        noteRepository.save(newNote);

        return newNote;
    }

    void addScript(Long noteId, MultipartFile file) {
        String fileName = fileUploader.upload(file);
        String newScript = sttConverter.convert(fileName);

        Note existNote = findNoteById(noteId);

        Note updatedNote = existNote.addScript(newScript);
        noteRepository.save(updatedNote);
    }

    @Transactional
    void addScript(Long noteId, String script) {
        Note existNote = findNoteById(noteId);

        Note updatedNote = existNote.addScript(script);
        noteRepository.save(updatedNote);
    }

    @Transactional
    Note update(Long noteId, String title) {
        Note existNote = findNoteById(noteId);

        Note updatedNote = existNote.update(title, clockProvider.millis());
        noteRepository.save(updatedNote);

        return updatedNote;
    }

    @Transactional
    void delete(List<Long> noteIds) {
        List<Note> existNotes = noteRepository.findAllByIdInAndIsDeleted(noteIds, false);

        for (Note existNote : existNotes) {
            Note deletedNote = existNote.delete(clockProvider.millis());
            noteRepository.save(deletedNote);
        }
    }

    @Transactional
    void restore(List<Long> noteIds) {
        List<Note> deletedNotes = noteRepository.findAllByIdInAndIsDeleted(noteIds, true);

        for (Note deletedNote : deletedNotes) {
            Note restoredNote = deletedNote.restore(clockProvider.millis());
            noteRepository.save(restoredNote);
        }
    }

    private Note findNoteById(Long noteId) {
        return noteRepository.findById(noteId).orElseThrow(ErrorCode.NOTE_NOT_FOUNDED::toException);
    }

}
