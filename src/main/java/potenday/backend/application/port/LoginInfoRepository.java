package potenday.backend.application.port;

import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;

import java.util.Optional;

public interface LoginInfoRepository {

    void save(LoginInfo loginInfo);

    Optional<LoginInfo> findByUserIdAndMethod(String userId, LoginMethod method);

    Optional<LoginInfo> findByMethodAndLoginKey(LoginMethod method, String loginKey);

}
