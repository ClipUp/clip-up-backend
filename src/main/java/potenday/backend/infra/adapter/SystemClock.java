package potenday.backend.infra.adapter;

import org.springframework.stereotype.Component;
import potenday.backend.application.port.ClockProvider;

import java.time.Clock;

@Component
class SystemClock implements ClockProvider {

    @Override
    public long millis() {
        return Clock.systemUTC().millis();
    }

}
