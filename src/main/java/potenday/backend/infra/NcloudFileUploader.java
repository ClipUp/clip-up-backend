package potenday.backend.infra;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.FileUploader;

@Component
class NcloudFileUploader implements FileUploader {

    @Override
    public String upload(MultipartFile file) {
        return "";
    }

}
