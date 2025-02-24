package potenday.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import potenday.backend.support.ErrorCode;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DynamicInsert
@DynamicUpdate
public class Note {

    private static int NOTE_TITLE_LENGTH = 40;

    @Id
    private Long id;

    private String title;

    private String script;

    private String audioFileUrl;

    private String content;

    private Boolean isFinished;

    private Long createTime;

    private Long updateTime;

    private Boolean isDeleted;

    public static Note create(Long id, Long currentTime) {
        return Note.builder()
            .id(id)
            .isFinished(false)
            .createTime(currentTime)
            .updateTime(currentTime)
            .isDeleted(false)
            .build();
    }

    public Note addScript(String script) {
        checkAlreadyFinished();

        this.script += "\n" + script;

        return this;
    }

    public Note finish(String audioFileUrl, String content, Long currentTime) {
        checkAlreadyFinished();

        this.title = content.substring(0, NOTE_TITLE_LENGTH);
        this.audioFileUrl = audioFileUrl;
        this.content = content;
        this.isFinished = true;
        this.createTime = currentTime;

        return this;
    }

    public Note update(String title, Long currentTime) {
        this.title = title;
        this.updateTime = currentTime;

        return this;
    }

    public Note delete(Long currentTime) {
        this.updateTime = currentTime;
        this.isDeleted = true;

        return this;
    }

    public Note restore(Long currentTime) {
        this.updateTime = currentTime;
        this.isDeleted = false;

        return this;
    }

    private void checkAlreadyFinished() {
        if (isFinished) {
            throw ErrorCode.ALREADY_FINISHED_NOTE.toException();
        }
    }

}
