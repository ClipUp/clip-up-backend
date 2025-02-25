package potenday.backend.application;

import org.springframework.web.multipart.MultipartFile;

public interface AudioUtil {

    int getDuration(MultipartFile audioFile);

    MultipartFile convertToMp3(MultipartFile audioFile);

}
