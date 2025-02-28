package potenday.backend.infra.adapter.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

interface UserEntityRepository extends MongoRepository<UserEntity, String> {

    boolean existsByEmail(String email);

}
