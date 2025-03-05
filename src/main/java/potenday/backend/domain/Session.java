package potenday.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(schema = "clip_up", name = "service_session")
@DynamicInsert
@DynamicUpdate
public class Session {

    @Id
    private String id;
    private String userId;
    private String token;
    private Boolean isBlocked;
    private Long createTime;
    private Long updateTime;

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
        this.token = token;
        this.updateTime = currentTime;

        return this;
    }

}
