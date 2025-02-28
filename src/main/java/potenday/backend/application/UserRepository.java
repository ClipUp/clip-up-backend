package potenday.backend.application;

import org.springframework.stereotype.Repository;
import potenday.backend.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository {

    void save(User user);

    boolean existsById(String id);

    boolean existsByEmail(String email);

    Optional<User> findById(String id);

}
