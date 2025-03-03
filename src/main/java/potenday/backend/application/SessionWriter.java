package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.application.port.ClockProvider;
import potenday.backend.application.port.IdProvider;
import potenday.backend.application.port.SessionRepository;
import potenday.backend.domain.Session;

@RequiredArgsConstructor
@Component
class SessionWriter {

    private final IdProvider idProvider;
    private final ClockProvider clockProvider;
    private final SessionRepository sessionRepository;

    void create(String userId, String token) {
        Session newSession = Session.create(idProvider.nextId(), userId, token, clockProvider.millis());
        sessionRepository.save(newSession);
    }

    void update(Session session, String token) {
        Session updatedSession = session.update(token, clockProvider.millis());
        sessionRepository.save(updatedSession);
    }

    @Transactional
    void delete(String userId) {
        sessionRepository.delete(userId);
    }

}
