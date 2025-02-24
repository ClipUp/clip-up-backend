package potenday.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import potenday.backend.application.UserService;
import potenday.backend.web.request.UserUpdateRequest;
import potenday.backend.web.response.UserResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
class UserController {

    private final UserService userService;

    @GetMapping
    UserResponse read(@AuthenticationPrincipal Long userId) {
        return UserResponse.from(userService.readUser(userId));
    }

    @PutMapping
    UserResponse update(@AuthenticationPrincipal Long userId, @RequestBody @Valid UserUpdateRequest request) {
        return UserResponse.from(userService.updateUser(userId, request.email(), request.username()));
    }

}
