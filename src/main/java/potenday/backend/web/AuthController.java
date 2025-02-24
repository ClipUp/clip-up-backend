package potenday.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import potenday.backend.application.AuthService;
import potenday.backend.web.request.LoginRequest;
import potenday.backend.web.request.PasswordUpdateRequest;
import potenday.backend.web.request.RegisterRequest;
import potenday.backend.web.response.TokenResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final long REFRESH_TOKEN_EXPIRES_IN = 7 * 24 * 60 * 60;

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request.email(), request.password(), request.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/password")
    void updatePassword(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Valid PasswordUpdateRequest request
    ) {
        authService.updatePassword(userId, request.originalPassword(), request.newPassword());
    }

    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        String[] tokens = authService.login(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, setRefreshTokenCookie(tokens[1]))
            .body(TokenResponse.of(tokens[0]));
    }

    @PostMapping("/token")
    ResponseEntity<TokenResponse> login(@CookieValue(value = REFRESH_TOKEN_KEY, required = false) String refreshToken) {
        String[] tokens = authService.reissueToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, setRefreshTokenCookie(tokens[1]))
            .body(TokenResponse.of(tokens[0]));
    }

    private String setRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
            .httpOnly(true)
            .secure(false)
            .sameSite("None")
            .path("/")
            .maxAge(REFRESH_TOKEN_EXPIRES_IN)
            .build()
            .toString();
    }

}
