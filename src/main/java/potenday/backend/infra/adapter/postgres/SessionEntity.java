package potenday.backend.infra.adapter.postgres;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import potenday.backend.domain.Session;

@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(schema = "clip_up", name = "service_session")
@DynamicInsert
@DynamicUpdate
class SessionEntity {

    @Id
    private String id;
    private String userId;
    private String token;
    private Boolean isBlocked;
    private Long createTime;
    private Long updateTime;

    static SessionEntity from(Session session) {
        return SessionEntity.builder()
            .id(session.id())
            .userId(session.userId())
            .token(session.token())
            .isBlocked(session.isBlocked())
            .createTime(session.createTime())
            .updateTime(session.updateTime())
            .build();
    }

    Session toSession() {
        return Session.builder()
            .id(this.id)
            .userId(this.userId)
            .token(this.token)
            .isBlocked(this.isBlocked)
            .createTime(this.createTime)
            .updateTime(this.updateTime)
            .build();
    }

}
