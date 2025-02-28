package potenday.backend.infra.repository;

import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;

@Builder(access = AccessLevel.PRIVATE)
@Document
class LoginInfoEntity {

    @Id
    private String id;
    private String userId;
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
