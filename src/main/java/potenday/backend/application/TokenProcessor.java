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

    private final TokenProvider tokenProvider;

    String issueAccessToken(String userId) {
        return issueToken(userId, TokenType.ACCESS);
    }

    String issueRefreshToken(String userId) {
        return issueToken(userId, TokenType.REFRESH);
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

    private String issueToken(String userId, TokenType tokenType) {
        Map<String, Object> payload = Map.of("userId", userId);
        return tokenProvider.issueToken(payload, tokenType.getExpiration());
    }

    private enum TokenType {
        ACCESS(Duration.ofHours(1)),
        REFRESH(Duration.ofDays(7));

        private final Duration expiration;

        TokenType(Duration expiration) {
            this.expiration = expiration;
        }

        Duration getExpiration() {
            return expiration;
        }
    }

}
