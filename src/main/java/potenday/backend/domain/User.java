package potenday.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DynamicInsert
@DynamicUpdate
public class User {

    @Id
    private Long id;

    private String email;

    private String username;

    private Long createTime;

    private Long updateTime;

    public static User create(Long id, String email, String username, Long currentTime) {
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
