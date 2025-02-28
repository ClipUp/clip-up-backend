package potenday.backend.infra.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import potenday.backend.application.LoginInfoRepository;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
class LoginInfoRepositoryImpl implements LoginInfoRepository {

    private final LoginInfoEntityRepository loginInfoEntityRepository;

    @Override
    public void save(LoginInfo loginInfo) {
        loginInfoEntityRepository.save(LoginInfoEntity.from(loginInfo));
    }

    @Override
    public Optional<LoginInfo> findByUserIdAndMethod(String userId, LoginMethod method) {
        return loginInfoEntityRepository.findByUserIdAndMethod(userId, method).map(LoginInfoEntity::toLoginInfo);
    }

    @Override
    public Optional<LoginInfo> findByMethodAndLoginKey(LoginMethod method, String loginKey) {
        var a = loginInfoEntityRepository.findByMethodAndLoginKey(method, loginKey);

        return a.map(LoginInfoEntity::toLoginInfo);
    }

}
