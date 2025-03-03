package potenday.backend.infra.adapter.postgres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import potenday.backend.application.port.SessionRepository;
import potenday.backend.domain.Session;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
class SessionRepositoryImpl implements SessionRepository {

    private final SessionEntityRepository sessionEntityRepository;
    
    @Override
    public void save(Session session) {
        sessionEntityRepository.save(SessionEntity.from(session));
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return sessionEntityRepository.findByToken(token).map(SessionEntity::toSession);
    }

    @Override
    public void delete(String userId) {
        sessionEntityRepository.deleteByUserId(userId);
    }

}
