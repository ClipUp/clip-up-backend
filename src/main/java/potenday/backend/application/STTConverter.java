package potenday.backend.application;


import potenday.backend.domain.Dialogue;

import java.util.List;

public interface STTConverter {

    List<Dialogue> convert(String fileUrl);

}
