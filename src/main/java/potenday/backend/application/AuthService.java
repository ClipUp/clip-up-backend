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

    @Transactional
    public void register(String email, String password, String username) {
        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.getId(), email, password);
    }

    public void updatePassword(String userId, String originalPassword, String newPassword) {
        loginInfoWriter.update(userId, originalPassword, newPassword);
    }

    public Tokens login(String email, String password) {
        LoginInfo existLoginInfo = loginInfoReader.read(email, password);

        Tokens tokens = issueToken(existLoginInfo.getUserId());

        sessionWriter.create(existLoginInfo.getUserId(), tokens.refreshToken());

        return tokens;
    }

    public void logout(String userId) {
        sessionWriter.delete(userId);
    }

    @Transactional
    public Tokens login(LoginMethod method, String loginKey, String email, String username) {
        Optional<LoginInfo> existLoginInfo = loginInfoReader.read(method, loginKey);
        if (existLoginInfo.isPresent()) {
            return issueToken(existLoginInfo.get().getUserId());
        }

        User newUser = register(method, loginKey, email, username);

        Tokens tokens = issueToken(newUser.getId());
        sessionWriter.create(newUser.getId(), tokens.refreshToken());

        return tokens;
    }

    @Transactional
    public Tokens reissueToken(String refreshToken) {
        Session existSession = sessionReader.read(refreshToken);
        if (existSession.getIsBlocked()) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }

        Tokens tokens = issueToken(existSession.getUserId());

        sessionWriter.update(existSession, tokens.refreshToken());

        return tokens;
    }

    public Optional<String> getUserId(String accessToken) {
        return tokenProcessor.getUserId(accessToken);
    }

    private User register(LoginMethod method, String loginKey, String email, String username) {
        User newUser = userWriter.create(email, username);
        loginInfoWriter.create(newUser.getId(), method, loginKey);
        return newUser;
    }

    private Tokens issueToken(String userId) {
        String accessToken = tokenProcessor.issueAccessToken(userId);
        String refreshToken = tokenProcessor.issueRefreshToken();

        return Tokens.of(accessToken, refreshToken);
    }

}
