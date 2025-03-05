package potenday.backend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import potenday.backend.application.UserService;
import potenday.backend.web.response.UserResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
class UserController {

    private final UserService userService;

    @GetMapping
    UserResponse readUser(@AuthenticationPrincipal String userId) {
        return UserResponse.from(userService.readUser(userId));
    }

}
