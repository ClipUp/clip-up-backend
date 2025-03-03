package potenday.backend.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record Session(
    String id,
    String userId,
    String token,
    Boolean isBlocked,
    Long createTime,
    Long updateTime
) {

    public static Session create(String id, String userId, String token, Long currentTime) {
        return Session.builder()
            .id(id)
            .userId(userId)
            .token(token)
            .isBlocked(false)
            .createTime(currentTime)
            .updateTime(currentTime)
            .build();
    }

    public Session update(String token, Long currentTime) {
        return toBuilder().token(token).updateTime(currentTime).build();
    }

}
