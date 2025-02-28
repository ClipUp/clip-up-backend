package potenday.backend.infra.adapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.EncoderProvider;

@Component
public class BCrypt implements EncoderProvider {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String text) {
        return bCryptPasswordEncoder.encode(text);
    }

    @Override
    public boolean matches(String text, String encodedText) {
        return !bCryptPasswordEncoder.matches(text, encodedText);
    }

}
