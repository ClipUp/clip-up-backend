package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;
import potenday.backend.domain.repository.LoginInfoRepository;
import potenday.backend.support.ErrorCode;

@RequiredArgsConstructor
@Component
class LoginInfoWriter {

    private final IdProvider idProvider;
    private final EncoderProvider encoderProvider;
    private final LoginInfoRepository loginInfoRepository;

    void create(Long userId, String email, String password) {
        LoginInfo newLoginInfo = LoginInfo.create(idProvider.nextId(), userId, email, encoderProvider.encode(password));
        loginInfoRepository.save(newLoginInfo);
    }

    void create(Long userId, LoginMethod method, String loginKey) {
        if (method.equals(LoginMethod.EMAIL)) {
            throw new IllegalArgumentException();
        }
        
        LoginInfo newLoginInfo = LoginInfo.create(idProvider.nextId(), userId, method, loginKey);
        loginInfoRepository.save(newLoginInfo);
    }

    @Transactional
    void update(Long userId, String originalPassword, String newPassword) {
        LoginInfo existLoginInfo = loginInfoRepository.findByUserIdAndMethod(userId, LoginMethod.EMAIL)
            .orElseThrow(ErrorCode.USER_NOT_FOUNDED::toException);

        if (!encoderProvider.matches(originalPassword, existLoginInfo.getPassword())) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        LoginInfo updatedLoginInfo = existLoginInfo.update(encoderProvider.encode(newPassword));
        loginInfoRepository.save(updatedLoginInfo);
    }

}
