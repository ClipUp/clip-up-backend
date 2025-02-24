package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
class TokenProcessor {

    private static final Duration ACCESS_TOKEN_EXPIRES_IN = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_EXPIRES_IN = Duration.ofDays(7);

    private final TokenProvider tokenProvider;

    String issueAccessToken(String userId) {
        Map<String, Object> payload = Map.of("userId", userId);
        return tokenProvider.issueToken(payload, ACCESS_TOKEN_EXPIRES_IN);
    }

    String issueRefreshToken(String userId) {
        Map<String, Object> payload = Map.of("userId", userId);
        return tokenProvider.issueToken(payload, REFRESH_TOKEN_EXPIRES_IN);
    }

    Optional<String> getUserId(String token) {
        try {
            Map<String, Object> payload = tokenProvider.getPayload(token);
            return Optional.of((String) payload.get("userId"));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

}
