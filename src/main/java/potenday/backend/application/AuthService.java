package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import potenday.backend.application.dto.Tokens;
import potenday.backend.domain.LoginInfo;
import potenday.backend.domain.LoginMethod;
import potenday.backend.domain.Session;
import potenday.backend.domain.User;
import potenday.backend.support.exception.ErrorCode;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final LoginInfoReader loginInfoReader;
    private final LoginInfoWriter loginInfoWriter;
    private final UserWriter userWriter;
    private final SessionReader sessionReader;
    private final SessionWriter sessionWriter;
    private final TokenProcessor tokenProcessor;
    private final UserReader userReader;
    private final EmailCodeProcessor emailCodeProcessor;

    public void sendEmail(String email) {
        userReader.validateAlreadyUsedEmail(email);
        emailCodeProcessor.sendCode(email);
    }

    public void validateEmail(String email, String code) {
        emailCodeProcessor.validateCode(email, code);
    }

    @Transactional
    public void register(String email, String password, String username) {
        emailCodeProcessor.deleteCode(email);
        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.id(), email, password);
    }

    public void updatePassword(String userId, String originalPassword, String newPassword) {
        loginInfoWriter.update(userId, originalPassword, newPassword);
    }

    public Tokens login(String email, String password) {
        LoginInfo existLoginInfo = loginInfoReader.read(email, password);

        Tokens tokens = issueToken(existLoginInfo.userId());

        sessionWriter.create(existLoginInfo.userId(), tokens.refreshToken());

        return tokens;
    }

    public void logout(String userId) {
        sessionWriter.delete(userId);
    }

    @Transactional
    public Tokens login(LoginMethod method, String loginKey, String email, String username) {
        Optional<LoginInfo> existLoginInfo = loginInfoReader.read(method, loginKey);
        if (existLoginInfo.isPresent()) {
            return issueToken(existLoginInfo.get().userId());
        }

        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.id(), method, loginKey);

        Tokens tokens = issueToken(newUser.id());

        sessionWriter.create(newUser.id(), tokens.refreshToken());

        return tokens;
    }

    @Transactional
    public Tokens reissueToken(String refreshToken) {
        Session existSession = sessionReader.read(refreshToken);
        if (existSession.isBlocked()) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        Tokens tokens = issueToken(existSession.userId());

        sessionWriter.update(existSession, tokens.refreshToken());

        return tokens;
    }

    public Optional<String> getUserId(String accessToken) {
        return tokenProcessor.getUserId(accessToken);
    }

    private Tokens issueToken(String userId) {
        String accessToken = tokenProcessor.issueAccessToken(userId);
        String refreshToken = tokenProcessor.issueRefreshToken();

        return Tokens.of(accessToken, refreshToken);
    }

}
