package potenday.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import potenday.backend.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

}
