package potenday.backend.infra.provider;

import org.springframework.stereotype.Component;
import potenday.backend.application.ClockProvider;

import java.time.Clock;

@Component
class SystemClock implements ClockProvider {

    @Override
    public long millis() {
        return Clock.systemUTC().millis();
    }

}
