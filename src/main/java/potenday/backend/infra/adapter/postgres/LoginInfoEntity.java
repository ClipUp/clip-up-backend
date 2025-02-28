package potenday.backend.infra.adapter.postgres;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;

@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(schema = "clip_up", name = "login_info")
class LoginInfoEntity {

    @Id
    private String id;
    private String userId;
    @Enumerated(EnumType.STRING)
    private LoginMethod method;
    private String loginKey;
    private String password;

    static LoginInfoEntity from(LoginInfo loginInfo) {
        return LoginInfoEntity.builder()
            .id(loginInfo.id())
            .userId(loginInfo.userId())
            .method(loginInfo.method())
            .loginKey(loginInfo.loginKey())
            .password(loginInfo.password())
            .build();
    }

    LoginInfo toLoginInfo() {
        return LoginInfo.builder()
            .id(this.id)
            .userId(this.userId)
            .method(this.method)
            .loginKey(this.loginKey)
            .password(this.password)
            .build();
    }

}
