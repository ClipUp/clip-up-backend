package potenday.backend.infra;

import org.springframework.stereotype.Component;
import potenday.backend.application.UUIDProvider;

import java.util.UUID;

@Component
class SystemUUID implements UUIDProvider {

    @Override
    public String getUUID() {
        return UUID.randomUUID().toString();
    }

}
