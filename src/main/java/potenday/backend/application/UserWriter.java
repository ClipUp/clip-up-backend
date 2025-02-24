package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.domain.User;
import potenday.backend.domain.repository.UserRepository;
import potenday.backend.support.ErrorCode;

@RequiredArgsConstructor
@Component
class UserWriter {

    private final IdProvider idProvider;
    private final ClockProvider clockProvider;
    private final UserRepository userRepository;

    @Transactional
    User create(String email, String username) {
        checkAlreadyUsedEmail(email);

        User newUser = User.create(idProvider.nextId(), email, username, clockProvider.millis());
        userRepository.save(newUser);

        return newUser;
    }

    @Transactional
    User update(Long id, String email, String username) {
        User existUser = userRepository.findById(id).orElseThrow(ErrorCode.USER_NOT_FOUNDED::toException);

        if (!existUser.getEmail().equals(email)) {
            checkAlreadyUsedEmail(email);
        }

        User updatedUser = existUser.update(email, username, clockProvider.millis());
        userRepository.save(updatedUser);

        return updatedUser;
    }

    private void checkAlreadyUsedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorCode.ALREADY_USED_EMAIL.toException();
        }
    }

}
