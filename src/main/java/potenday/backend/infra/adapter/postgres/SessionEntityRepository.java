package potenday.backend.infra.adapter.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SessionEntityRepository extends JpaRepository<SessionEntity, Long> {

    Optional<SessionEntity> findByToken(String token);

    void deleteByUserId(String userId);

}
