package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.domain.Session;
import potenday.backend.domain.repository.SessionRepository;
import potenday.backend.support.exception.ErrorCode;

@RequiredArgsConstructor
@Component
class SessionReader {

    private final SessionRepository sessionRepository;

    Session read(String token) {
        return sessionRepository.findByToken(token).orElseThrow(ErrorCode.UNAUTHORIZED::toException);
    }

}
