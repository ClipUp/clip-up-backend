package potenday.backend.infra;

import org.springframework.stereotype.Component;
import potenday.backend.application.STTConverter;

@Component
class ClovaSpeech implements STTConverter {

    @Override
    public String convert(String fileName) {
        return "";
    }

}
