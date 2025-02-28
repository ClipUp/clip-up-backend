package potenday.backend.web.response;

import potenday.backend.domain.User;

public record UserResponse(
    String id,
    String email,
    String username
) {

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.email(), user.username());
    }

}
