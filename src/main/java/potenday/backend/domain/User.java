package potenday.backend.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record User(
    String id,
    String email,
    String username,
    Long createTime,
    Long updateTime
) {

    public static User create(String id, String email, String username, Long currentTime) {
        return User.builder()
            .id(id)
            .email(email)
            .username(username)
            .createTime(currentTime)
            .updateTime(currentTime)
            .build();
    }

}
