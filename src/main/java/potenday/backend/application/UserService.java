package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import potenday.backend.domain.User;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserReader userReader;
    private final UserWriter userWriter;

    public User readUser(String userId) {
        return userReader.read(userId);
    }

    public User updateUser(String userId, String email, String username) {
        return userWriter.update(userId, email, username);
    }

}
