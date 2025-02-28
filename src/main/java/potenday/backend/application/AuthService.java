package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;
import potenday.backend.domain.User;
import potenday.backend.support.exception.ErrorCode;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final LoginInfoReader loginInfoReader;
    private final LoginInfoWriter loginInfoWriter;
    private final UserWriter userWriter;
    private final TokenProcessor tokenProcessor;

    @Transactional
    public void register(String email, String password, String username) {
        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.id(), email, password);
    }

    public void updatePassword(String userId, String originalPassword, String newPassword) {
        loginInfoWriter.update(userId, originalPassword, newPassword);
    }

    public String[] login(String email, String password) {
        LoginInfo existLoginInfo = loginInfoReader.read(email, password);

        return issueToken(existLoginInfo.userId());
    }

    @Transactional
    public String[] login(LoginMethod method, String loginKey, String email, String username) {
        Optional<LoginInfo> existLoginInfo = loginInfoReader.read(method, loginKey);
        if (existLoginInfo.isPresent()) {
            return issueToken(existLoginInfo.get().userId());
        }

        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.id(), method, loginKey);

        return issueToken(newUser.id());
    }

    public String[] reissueToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        String userId = tokenProcessor.getUserId(refreshToken).orElseThrow(ErrorCode.UNAUTHORIZED::toException);

        String accessToken = tokenProcessor.issueRefreshToken(userId);

        return new String[]{accessToken, refreshToken};
    }

    public Optional<String> getUserId(String accessToken) {
        return tokenProcessor.getUserId(accessToken);
    }

    private String[] issueToken(String userId) {
        String accessToken = tokenProcessor.issueAccessToken(userId);
        String refreshToken = tokenProcessor.issueRefreshToken(userId);

        return new String[]{accessToken, refreshToken};
    }

}
