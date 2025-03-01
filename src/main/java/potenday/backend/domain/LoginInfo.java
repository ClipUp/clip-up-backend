package potenday.backend.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record LoginInfo(
    String id,
    String userId,
    LoginMethod method,
    String loginKey,
    String password
) {

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
        return this.toBuilder().password(password).build();
    }

    public LoginInfo updateLoginKey(String email) {
        return this.toBuilder().loginKey(email).build();
    }

}
