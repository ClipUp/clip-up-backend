package potenday.backend.application;

import java.util.List;

public interface ParagraphSplitter {

    List<String> apply(String text);

}

