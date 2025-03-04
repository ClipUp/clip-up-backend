package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import potenday.backend.domain.User;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserReader userReader;

    public User readUser(String userId) {
        return userReader.read(userId);
    }

}
