package potenday.backend.infra.adapter.postgres;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import potenday.backend.domain.User;

@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(schema = "clip_up", name = "service_user")
class UserEntity {

    @Id
    private String id;
    private String email;
    private String username;
    private Long createTime;
    private Long updateTime;

    static UserEntity from(User user) {
        return UserEntity.builder()
            .id(user.id())
            .email(user.email())
            .username(user.username())
            .createTime(user.createTime())
            .updateTime(user.updateTime())
            .build();
    }

    User toUser() {
        return User.builder()
            .id(this.id)
            .email(this.email)
            .username(this.username)
            .createTime(this.createTime)
            .updateTime(this.updateTime)
            .build();
    }

}
