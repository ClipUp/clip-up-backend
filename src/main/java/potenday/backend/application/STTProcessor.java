package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
class STTProcessor {

    private final FileUploader fileUploader;
    private final STTConverter sttConverter;

    String convert(MultipartFile file) {
        String fileName = fileUploader.upload(file);
        return sttConverter.convert(fileName);
    }

}
