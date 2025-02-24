package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.domain.Dialogue;
import potenday.backend.domain.Note;
import potenday.backend.domain.repository.NoteRepository;
import potenday.backend.support.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
class NoteWriter {

    private static final String FOLDER_NAME = "note-audio";
    private final IdProvider idProvider;
    private final ClockProvider clockProvider;
    private final FileUploader fileUploader;
    private final STTConverter sttConverter;
    private final ParagraphSplitter paragraphSplitter;
    private final ChatClient chatClient;
    private final NoteRepository noteRepository;

    Note create(MultipartFile audioFile) {
        String id = idProvider.nextId();

        String fileName = String.format("%s.mp3", id);
        String audioFileUrl = fileUploader.upload(audioFile, FOLDER_NAME, fileName);
        List<Dialogue> script = sttConverter.convert(String.format("%s/%s", FOLDER_NAME, fileName));

        String content = convertScriptToContent(script);

        Note newNote = Note.create(id, script, audioFileUrl, content, clockProvider.millis());
        noteRepository.save(newNote);

        return newNote;
    }

    @Transactional
    Note update(String id, String title) {
        Note existNote = findNoteById(id);

        Note updatedNote = existNote.update(title, clockProvider.millis());
        noteRepository.save(updatedNote);

        return updatedNote;
    }

    @Transactional
    void delete(List<String> ids) {
        List<Note> existNotes = noteRepository.findAllByIdInAndIsDeleted(ids, false);

        for (Note existNote : existNotes) {
            Note deletedNote = existNote.delete(clockProvider.millis());
            noteRepository.save(deletedNote);
        }
    }

    @Transactional
    void restore(List<String> ids) {
        List<Note> deletedNotes = noteRepository.findAllByIdInAndIsDeleted(ids, true);

        for (Note deletedNote : deletedNotes) {
            Note restoredNote = deletedNote.restore(clockProvider.millis());
            noteRepository.save(restoredNote);
        }
    }

    private Note findNoteById(String id) {
        return noteRepository.findById(id).orElseThrow(ErrorCode.NOTE_NOT_FOUNDED::toException);
    }

    private String convertScriptToContent(List<Dialogue> script) {
        List<String> paragraphs = paragraphSplitter.apply(Note.convertScriptToString(script));

        StringBuilder sb = new StringBuilder();
        for (String paragraph : paragraphs) {
            String note = chatClient.prompt()
                .system("음성 녹음 파일 업로드 시 인덱스 정보가 필요함\n" +
                    "음성 녹음 파일을 바이너리 파일 형식으로 변환하여 업로드함\n" +
                    "녹음 파일의 순서를 확인하고, 문단 나누기를 통해 음성 녹음 파일을 음성 시각화 프로그램으로 변환함\n" +
                    "클라이언트에서 음성 녹음 파일을 음성 회의록으로 변환 시킨다는 사실을 스크립트에 포함시킴\n" +
                    "서버에서 음성 녹음 파일을 변환하고, 인덱스 정보를 받음\n" + "위의 예시처럼 아래의 스크립트를 기반으로 회의록을 작성해줘.")
                .user(paragraph)
                .call()
                .content();
            sb.append(note);
        }
        return sb.toString();
    }

}
