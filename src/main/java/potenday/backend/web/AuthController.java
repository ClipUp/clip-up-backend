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
import potenday.backend.application.dto.Tokens;
import potenday.backend.support.exception.ErrorCode;
import potenday.backend.web.request.*;
import potenday.backend.web.response.TokenResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final long REFRESH_TOKEN_EXPIRES_IN = 7 * 24 * 60 * 60;

    private final AuthService authService;

    @PostMapping("/email")
    void sendEmail(@RequestBody @Valid EmailSendRequest request) {
        authService.sendEmail(request.email());
    }

    @PostMapping("/email/code")
    void validateEmail(@RequestBody @Valid EmailValidateRequest request) {
        authService.validateEmail(request.email(), request.code());
    }

    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request.email(), request.password(), request.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/password")
    void updatePassword(
        @AuthenticationPrincipal String userId,
        @RequestBody @Valid PasswordUpdateRequest request
    ) {
        authService.updatePassword(userId, request.originalPassword(), request.newPassword());
    }

    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        Tokens tokens = authService.login(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, setRefreshTokenCookie(tokens.refreshToken()))
            .body(TokenResponse.of(tokens.accessToken()));
    }

    @PostMapping("/logout")
    void logout(@AuthenticationPrincipal String userId) {
        authService.logout(userId);
    }

    @PostMapping("/token")
    ResponseEntity<TokenResponse> login(@CookieValue(value = REFRESH_TOKEN_KEY, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        Tokens tokens = authService.reissueToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, setRefreshTokenCookie(tokens.refreshToken()))
            .body(TokenResponse.of(tokens.accessToken()));
    }

    private String setRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(REFRESH_TOKEN_EXPIRES_IN)
            .build()
            .toString();
    }

    private String removeRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(-1)
            .build()
            .toString();
    }

}
