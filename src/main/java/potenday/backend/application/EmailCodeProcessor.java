package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.CodeProvider;
import potenday.backend.application.port.EmailCodeRepository;
import potenday.backend.application.port.MailProvider;
import potenday.backend.support.exception.ErrorCode;

import java.time.Duration;

@RequiredArgsConstructor
@Component
class EmailCodeProcessor {

    private static final int CODE_LENGTH = 6;
    private static final Duration CODE_EXPIRES_IN = Duration.ofMinutes(3);
    private static final String ADMIN_EMAIL = "qudgnl345@naver.com";

    private final MailProvider mailProvider;
    private final CodeProvider codeProvider;
    private final EmailCodeRepository emailCodeRepository;

    void sendCode(String email) {
        String code = codeProvider.generateNumCode(CODE_LENGTH);
        emailCodeRepository.save(email, code, CODE_EXPIRES_IN);
        mailProvider.send(ADMIN_EMAIL, email, "제목", code);
    }

    void validateCode(String email, String code) {
        String existCode = emailCodeRepository.findByEmail(email).orElseThrow(ErrorCode.UNAUTHORIZED::toException);
        if (!existCode.equals(code)) {
            throw ErrorCode.UNAUTHORIZED.toException();
        }
    }

    void deleteCode(String email) {
        emailCodeRepository.findByEmail(email).orElseThrow(ErrorCode.VALIDATE_EMAIL_FIRST::toException);
        emailCodeRepository.delete(email);
    }


}
