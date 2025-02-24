package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.domain.Note;

@RequiredArgsConstructor
@Service
public class NoteService {

    private final NoteWriter noteWriter;

    public Note crateNote(MultipartFile audioFile) {
        return noteWriter.create(audioFile);
    }

}
