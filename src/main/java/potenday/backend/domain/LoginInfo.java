package potenday.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class LoginInfo {

    @Id
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private LoginMethod method;

    private String loginKey;

    private String password;

    public static LoginInfo create(Long id, Long userId, LoginMethod method, String loginKey) {
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

    public static LoginInfo create(Long id, Long userId, String loginKey, String password) {
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
