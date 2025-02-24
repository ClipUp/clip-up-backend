package potenday.backend.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import potenday.backend.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByEmail(String email);

}
