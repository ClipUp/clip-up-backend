package potenday.backend.domain;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import potenday.backend.support.exception.ErrorCode;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(schema = "clip_up", name = "meeting")
@DynamicInsert
@DynamicUpdate
public class Meeting {

    private static int DEFAULT_TITLE_LENGTH = 40;
    @Id
    private String id;
    private String ownerId;
    private String title;
    private String audioFileUrl;
    private Integer audioFileDuration;
    @Convert(converter = ScriptConverter.class)
    private List<Dialogue> script;
    private String minutes;
    private Long createTime;
    private Long updateTime;
    private Boolean isDeleted;

    public static Meeting create(
        String id,
        String userId,
        String audioFileUrl,
        int audioFileDuration,
        List<Dialogue> script,
        String minutes,
        Long currentTime
    ) {
        return Meeting.builder()
            .id(id)
            .ownerId(userId)
            .audioFileUrl(audioFileUrl)
            .audioFileDuration(audioFileDuration)
            .script(script)
            .minutes(minutes)
            .title(extractTitle(minutes))
            .createTime(currentTime)
            .updateTime(currentTime)
            .isDeleted(false)
            .build();
    }

    private static String extractTitle(String minutes) {
        return minutes.length() > DEFAULT_TITLE_LENGTH ? minutes.substring(0, DEFAULT_TITLE_LENGTH) : minutes
    }

    public Meeting update(String userId, String title, Long currentTime) {
        checkOwner(userId);

        this.title = title;
        this.updateTime = currentTime;

        return this;
    }

    public Meeting delete(String userId, Long currentTime) {
        checkOwner(userId);

        this.isDeleted = true;
        this.updateTime = currentTime;

        return this;
    }

    public Meeting restore(String userId, Long currentTime) {
        checkOwner(userId);

        this.isDeleted = false;
        this.updateTime = currentTime;

        return this;
    }

    public Meeting migrate(String minutes) {
        this.title = extractTitle(minutes);
        this.minutes = minutes;

        return this;
    }

    private void checkOwner(String userId) {
        if (!userId.equals(ownerId)) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }
    }

}
