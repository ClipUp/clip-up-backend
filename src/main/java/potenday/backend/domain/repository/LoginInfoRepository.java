package potenday.backend.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;

import java.util.Optional;

@Repository
public interface LoginInfoRepository extends MongoRepository<LoginInfo, String> {

    Optional<LoginInfo> findByUserIdAndMethod(String userId, LoginMethod method);

    Optional<LoginInfo> findByMethodAndLoginKey(LoginMethod method, String loginKey);

}
