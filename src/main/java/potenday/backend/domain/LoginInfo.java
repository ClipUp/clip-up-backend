package potenday.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(schema = "clip_up", name = "login_info")
@DynamicInsert
@DynamicUpdate
public class LoginInfo {

    @Id
    private String id;
    private String userId;
    @Enumerated(EnumType.STRING)
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

    public LoginInfo updatePassword(String password) {
        this.password = password;

        return this;
    }

}
