package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.UserRepository;
import potenday.backend.domain.User;
import potenday.backend.support.exception.ErrorCode;

@RequiredArgsConstructor
@Component
class UserReader {

    private final UserRepository userRepository;

    void validateExistUser(String id) {
        if (!userRepository.existsById(id)) {
            throw ErrorCode.MEETING_NOT_FOUNDED.toException();
        }
    }

    void validateAlreadyUsedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorCode.ALREADY_USED_EMAIL.toException();
        }
    }

    User read(String id) {
        return userRepository.findById(id).orElseThrow(ErrorCode.USER_NOT_FOUNDED::toException);
    }

}
