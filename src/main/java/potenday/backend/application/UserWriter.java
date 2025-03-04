package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.application.port.ClockProvider;
import potenday.backend.application.port.IdProvider;
import potenday.backend.application.port.UserRepository;
import potenday.backend.domain.User;
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

    User update(String id, String email, String username) {
        User existUser = userRepository.findById(id).orElseThrow(ErrorCode.USER_NOT_FOUNDED::toException);

        if (!existUser.email().equals(email)) {
            validateAlreadyUsedEmail(email);
        }

        User updatedUser = existUser.update(email, username, clockProvider.millis());
        userRepository.save(updatedUser);

        return updatedUser;
    }

    private void validateAlreadyUsedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorCode.ALREADY_USED_EMAIL.toException();
        }
    }

}
