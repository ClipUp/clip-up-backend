package potenday.backend.infra.adapter.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import potenday.backend.domain.LoginMethod;

import java.util.Optional;

interface LoginInfoEntityRepository extends JpaRepository<LoginInfoEntity, String> {

    Optional<LoginInfoEntity> findByUserIdAndMethod(String userId, LoginMethod method);

    Optional<LoginInfoEntity> findByMethodAndLoginKey(LoginMethod method, String loginKey);

}
