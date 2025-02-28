package potenday.backend.infra.adapter.mongodb;

import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import potenday.backend.domain.User;

@Builder(access = AccessLevel.PRIVATE)
@Document(collation = "user")
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
