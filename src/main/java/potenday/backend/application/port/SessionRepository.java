package potenday.backend.application.port;


import potenday.backend.domain.Session;

import java.util.Optional;

public interface SessionRepository {

    void save(Session session);

    Optional<Session> findByToken(String token);

    void delete(String userId);

}
