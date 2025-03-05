package potenday.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import potenday.backend.domain.Session;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByToken(String token);

    void deleteByUserId(String userId);

}
