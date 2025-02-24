package potenday.backend.application;

import org.springframework.web.multipart.MultipartFile;

public interface SpeechToText {

    String call(MultipartFile file);

}
