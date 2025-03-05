package potenday.backend.application;

import java.time.Duration;
import java.util.Map;

public interface TokenProvider {

    String issueToken(Duration duration);

    String issueToken(Map<String, Object> claims, Duration duration);

    Map<String, Object> getPayload(String token);

}
