package potenday.backend.application;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String upload(MultipartFile file);

}
