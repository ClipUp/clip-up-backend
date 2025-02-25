package potenday.backend.application;

import org.springframework.stereotype.Component;
import potenday.backend.domain.Dialogue;

import java.util.List;

@Component
class MinutesProcessor {

    String generate(List<Dialogue> script) {
        StringBuilder sb = new StringBuilder();
        for (Dialogue dialogue : script) {
            sb.append(dialogue.toString()).append("\n");
        }
        return sb.toString();
    }

}
