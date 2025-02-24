package potenday.backend.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document
public class User {

    @Id
    private String id;
    private String email;
    private String username;
    private Long createTime;
    private Long updateTime;

    public static User create(String id, String email, String username, Long currentTime) {
        return User.builder()
            .id(id)
            .email(email)
            .username(username)
            .createTime(currentTime)
            .updateTime(currentTime)
            .build();
    }

    public User update(String email, String username, Long currentTime) {
        this.email = email;
        this.username = username;
        this.updateTime = currentTime;

        return this;
    }

}
