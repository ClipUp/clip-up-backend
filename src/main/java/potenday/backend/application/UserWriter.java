package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.domain.User;
import potenday.backend.domain.repository.UserRepository;
import potenday.backend.support.exception.ErrorCode;

@RequiredArgsConstructor
@Component
class UserWriter {

    private final IdProvider idProvider;
    private final ClockProvider clockProvider;
    private final UserRepository userRepository;

    @Transactional
    User create(String email, String username) {
        validateAlreadyUsedEmail(email);

        User newUser = User.create(idProvider.nextId(), email, username, clockProvider.millis());
        userRepository.save(newUser);

        return newUser;
    }

    private void validateAlreadyUsedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorCode.ALREADY_USED_EMAIL.toException();
        }
    }

}
