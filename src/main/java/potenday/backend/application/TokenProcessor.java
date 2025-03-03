package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.TokenProvider;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
class TokenProcessor {

    private static final Duration ACCESS_TOKEN_EXPIRES_IN = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_EXPIRES_IN = Duration.ofHours(1);

    private final TokenProvider tokenProvider;

    String issueAccessToken(String userId) {
        Map<String, Object> payload = Map.of("userId", userId);
        return tokenProvider.issueToken(payload, ACCESS_TOKEN_EXPIRES_IN);
    }

    String issueRefreshToken() {
        return tokenProvider.issueToken(REFRESH_TOKEN_EXPIRES_IN);
    }

    Optional<String> getUserId(String token) {
        try {
            Map<String, Object> payload = tokenProvider.getPayload(token);
            Object userId = payload.get("userId");
            return (userId instanceof String) ? Optional.of((String) userId) : Optional.empty();
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

}
