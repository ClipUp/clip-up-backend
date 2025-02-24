package potenday.backend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.NoteService;
import potenday.backend.web.response.NoteResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notes")
class NoteController {

    private final NoteService noteService;

    @PostMapping
    NoteResponse createNote(MultipartFile audioFile) {
        return NoteResponse.from(noteService.crateNote(audioFile));
    }

}
