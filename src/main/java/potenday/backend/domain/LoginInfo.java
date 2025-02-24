package potenday.backend.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document
public class LoginInfo {

    @Id
    private String id;
    private String userId;
    private LoginMethod method;
    private String loginKey;
    private String password;

    public static LoginInfo create(String id, String userId, LoginMethod method, String loginKey) {
        if (method.equals(LoginMethod.EMAIL)) {
            throw new IllegalArgumentException();
        }

        return LoginInfo.builder()
            .id(id)
            .userId(userId)
            .method(method)
            .loginKey(loginKey)
            .build();
    }

    public static LoginInfo create(String id, String userId, String loginKey, String password) {
        return LoginInfo.builder()
            .id(id)
            .userId(userId)
            .method(LoginMethod.EMAIL)
            .loginKey(loginKey)
            .password(password)
            .build();
    }

    public LoginInfo update(String password) {
        this.password = password;

        return this;
    }

}
