package potenday.backend.domain;

import lombok.*;
import lombok.Builder.Default;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document
public class Note {

    private static int NOTE_TITLE_LENGTH = 40;

    @Id
    private String id;
    private String title;
    @Default
    private List<Dialogue> script = new ArrayList<>();
    private String audioFileUrl;
    private String content;
    private Long createTime;
    private Long updateTime;
    private Boolean isDeleted;

    public static Note create(String id, List<Dialogue> script, String audioFileUrl, String content, Long currentTime) {
        return Note.builder()
            .id(id)
            .title(content.substring(0, NOTE_TITLE_LENGTH))
            .script(script)
            .audioFileUrl(audioFileUrl)
            .content(content)
            .createTime(currentTime)
            .updateTime(currentTime)
            .isDeleted(false)
            .build();
    }

    public static String convertScriptToString(List<Dialogue> script) {
        StringBuilder sb = new StringBuilder();
        for (Dialogue dialogue : script) {
            sb.append(dialogue.toString());
        }
        return sb.toString();
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

}
