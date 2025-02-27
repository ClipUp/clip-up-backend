package potenday.backend.application;

import org.springframework.web.multipart.MultipartFile;

public interface AudioUtil {

    Result convertToMp3(MultipartFile audioFile);

    record Result(int fileDuration, MultipartFile mp3File) {

    }

}
