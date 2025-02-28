package potenday.backend.infra.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

interface UserEntityRepository extends MongoRepository<UserEntity, String> {

    boolean existsByEmail(String email);

}
