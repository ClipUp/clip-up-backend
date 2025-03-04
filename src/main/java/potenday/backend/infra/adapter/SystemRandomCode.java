package potenday.backend.infra.adapter;

import org.springframework.stereotype.Component;
import potenday.backend.application.port.CodeProvider;

import java.util.Random;

@Component
class SystemRandomCode implements CodeProvider {

    private final Random random = new Random();

    @Override
    public String generateNumCode(int length) {
        int maxValue = (int) Math.pow(10, length);
        int randomNumber = random.nextInt(maxValue);
        return String.format("%0" + length + "d", randomNumber);
    }

}
