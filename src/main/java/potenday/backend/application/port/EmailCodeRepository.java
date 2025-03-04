package potenday.backend.application.port;

import java.time.Duration;
import java.util.Optional;

public interface EmailCodeRepository {

    void save(String email, String code, Duration duration);

    Optional<String> findByEmail(String email);

    void delete(String email);

}
