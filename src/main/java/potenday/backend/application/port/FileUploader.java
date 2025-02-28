package potenday.backend.application.port;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String upload(MultipartFile file, String folderName, String fileName);

}
