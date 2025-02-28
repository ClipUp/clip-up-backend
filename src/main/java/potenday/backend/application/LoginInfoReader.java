package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.EncoderProvider;
import potenday.backend.application.port.LoginInfoRepository;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;
import potenday.backend.support.exception.ErrorCode;

import java.util.Optional;

@RequiredArgsConstructor
@Component
class LoginInfoReader {

    private final EncoderProvider encoderProvider;
    private final LoginInfoRepository loginInfoRepository;

    LoginInfo read(String email, String password) {
        LoginInfo existLoginInfo = loginInfoRepository.findByMethodAndLoginKey(LoginMethod.EMAIL, email)
            .orElseThrow(ErrorCode.USER_NOT_FOUNDED::toException);

        if (!encoderProvider.matches(password, existLoginInfo.password())) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        return existLoginInfo;
    }

    Optional<LoginInfo> read(LoginMethod method, String loginKey) {
        if (method.equals(LoginMethod.EMAIL)) {
            throw new IllegalArgumentException();
        }

        return loginInfoRepository.findByMethodAndLoginKey(method, loginKey);
    }

}
