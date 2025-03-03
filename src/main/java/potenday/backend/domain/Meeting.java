package potenday.backend.domain;

import lombok.Builder;
import potenday.backend.support.exception.ErrorCode;

import java.util.List;

@Builder(toBuilder = true)
public record Meeting(
    String id,
    String ownerId,
    String title,
    String audioFileUrl,
    Integer audioFileDuration,
    List<Dialogue> script,
    String minutes,
    Long createTime,
    Long updateTime,
    Boolean isDeleted
) {

    private static int DEFAULT_TITLE_LENGTH = 40;

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
            .title(minutes.length() > DEFAULT_TITLE_LENGTH ? minutes.substring(0, DEFAULT_TITLE_LENGTH) : minutes)
            .createTime(currentTime)
            .updateTime(currentTime)
            .isDeleted(false)
            .build();
    }

    public Meeting update(String userId, String title, Long currentTime) {
        checkOwner(userId);

        return toBuilder().title(title).updateTime(currentTime).build();
    }

    public Meeting delete(String userId, Long currentTime) {
        checkOwner(userId);

        return toBuilder().isDeleted(true).updateTime(currentTime).build();
    }

    public Meeting restore(String userId, Long currentTime) {
        checkOwner(userId);

        return toBuilder().isDeleted(false).updateTime(currentTime).build();
    }

    private void checkOwner(String userId) {
        if (!userId.equals(ownerId)) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }
    }

}
