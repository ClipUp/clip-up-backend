package potenday.backend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import potenday.backend.support.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@Document
public class Meeting {

    private static int DEFAULT_TITLE_LENGTH = 40;

    private String id;
    private String ownerId;
    private String title;
    private String audioFileUrl;
    private Integer audioFileDuration;
    @Default
    private List<Dialogue> script = new ArrayList<>();
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
            .title("test")
            .createTime(currentTime)
            .updateTime(currentTime)
            .isDeleted(false)
            .build();
    }

    public Meeting update(String userId, String title, Long currentTime) {
        checkOwner(userId);

        this.title = title;
        this.createTime = currentTime;

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

    private void checkOwner(String userId) {
        if (!userId.equals(ownerId)) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }
    }

}
