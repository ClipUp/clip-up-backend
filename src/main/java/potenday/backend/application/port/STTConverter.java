package potenday.backend.application.port;


import potenday.backend.domain.Dialogue;

import java.util.List;

public interface STTConverter {

    List<Dialogue> convert(String fileUrl);

}
