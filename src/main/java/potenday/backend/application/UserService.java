package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.domain.User;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final LoginInfoWriter loginInfoWriter;

    public User readUser(String userId) {
        return userReader.read(userId);
    }

    @Transactional
    public User updateUser(String userId, String email, String username) {
        loginInfoWriter.update(userId, email);
        return userWriter.update(userId, email, username);
    }

}
