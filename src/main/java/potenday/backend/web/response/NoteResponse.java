package potenday.backend.web.response;

import potenday.backend.domain.Note;

import java.util.List;

public record NoteResponse(
    String id,
    String title,
    List<DialogueResponse> script,
    String audioFileUrl,
    String content,
    Long createTime,
    Long updateTime
) {

    public static NoteResponse from(Note note) {
        return new NoteResponse(
            note.getId(),
            note.getTitle(),
            note.getScript().stream().map(DialogueResponse::from).toList(),
            note.getAudioFileUrl(),
            note.getContent(),
            note.getCreateTime(),
            note.getUpdateTime()
        );
    }

}
