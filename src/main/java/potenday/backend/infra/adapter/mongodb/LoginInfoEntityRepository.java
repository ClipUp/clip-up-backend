package potenday.backend.infra.adapter.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import potenday.backend.domain.LoginMethod;

import java.util.Optional;

interface LoginInfoEntityRepository extends MongoRepository<LoginInfoEntity, String> {

    Optional<LoginInfoEntity> findByUserIdAndMethod(String userId, LoginMethod method);

    Optional<LoginInfoEntity> findByMethodAndLoginKey(LoginMethod method, String loginKey);

}
