package potenday.backend.infra;

import org.springframework.stereotype.Component;
import potenday.backend.application.ParagraphSplitter;

import java.util.List;

@Component
class ClovaParagraphSplitter implements ParagraphSplitter {

    @Override
    public List<String> apply(String text) {
        return List.of();
    }

}
