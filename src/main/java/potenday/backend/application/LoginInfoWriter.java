package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;
import potenday.backend.support.exception.ErrorCode;

@RequiredArgsConstructor
@Component
class LoginInfoWriter {

    private final IdProvider idProvider;
    private final EncoderProvider encoderProvider;
    private final LoginInfoRepository loginInfoRepository;

    void create(String userId, String email, String password) {
        LoginInfo newLoginInfo = LoginInfo.create(idProvider.nextId(), userId, email, encoderProvider.encode(password));
        loginInfoRepository.save(newLoginInfo);
    }

    void create(String userId, LoginMethod method, String loginKey) {
        if (method.equals(LoginMethod.EMAIL)) {
            throw new IllegalArgumentException();
        }

        LoginInfo newLoginInfo = LoginInfo.create(idProvider.nextId(), userId, method, loginKey);
        loginInfoRepository.save(newLoginInfo);
    }

    @Transactional
    void update(String userId, String originalPassword, String newPassword) {
        LoginInfo existLoginInfo = loginInfoRepository.findByUserIdAndMethod(userId, LoginMethod.EMAIL)
            .orElseThrow(ErrorCode.USER_NOT_FOUNDED::toException);

        if (encoderProvider.matches(originalPassword, existLoginInfo.password())) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        LoginInfo updatedLoginInfo = existLoginInfo.update(encoderProvider.encode(newPassword));
        loginInfoRepository.save(updatedLoginInfo);
    }

}
