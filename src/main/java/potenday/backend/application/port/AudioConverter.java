package potenday.backend.application.port;

import org.springframework.web.multipart.MultipartFile;

public interface AudioConverter {

    Result convertToMp3(MultipartFile audioFile);

    record Result(int fileDuration, MultipartFile mp3File) {

    }

}
