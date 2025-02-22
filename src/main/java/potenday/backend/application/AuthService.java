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
        loginInfoWriter.create(newUser.getId(), email, password);
    }

    public void updatePassword(Long userId, String originalPassword, String newPassword) {
        loginInfoWriter.update(userId, originalPassword, newPassword);
    }

    public String[] login(String email, String password) {
        LoginInfo existLoginInfo = loginInfoReader.read(email, password);

        return issueToken(existLoginInfo.getUserId());
    }

    @Transactional
    public String[] login(LoginMethod method, String loginKey, String email, String username) {
        Optional<LoginInfo> existLoginInfo = loginInfoReader.read(method, loginKey);
        if (existLoginInfo.isPresent()) {
            return issueToken(existLoginInfo.get().getUserId());
        }

        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.getId(), method, loginKey);

        return issueToken(newUser.getId());
    }

    public String[] reissueToken(String refreshToken) {
        Long userId = tokenProcessor.getUserId(refreshToken).orElseThrow(ErrorCode.UNAUTHORIZED::toException);

        String accessToken = tokenProcessor.issueRefreshToken(userId);

        return new String[]{accessToken, refreshToken};
    }

    public Optional<Long> getUserId(String accessToken) {
        return tokenProcessor.getUserId(accessToken);
    }

    private String[] issueToken(Long userId) {
        String accessToken = tokenProcessor.issueAccessToken(userId);
        String refreshToken = tokenProcessor.issueRefreshToken(userId);

        return new String[]{accessToken, refreshToken};
    }

}
