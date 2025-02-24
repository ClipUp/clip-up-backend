package potenday.backend.infra;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.FileUploader;
import potenday.backend.support.ErrorCode;

import java.io.IOException;

@RequiredArgsConstructor
@Component
class NCPFileUploader implements FileUploader {

    private static final String DEFAULT_BUCKET_NAME = "clip-up";

    private final AmazonS3Client objectStorageClient;

    @Override
    public String upload(MultipartFile file, String folderName, String fileName) {
        String bucketName = String.format("%s/%s", DEFAULT_BUCKET_NAME, folderName);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.PublicRead);
            objectStorageClient.putObject(request);
        } catch (IOException e) {
            throw ErrorCode.INTERNAL_SERVER_ERROR.toException();
        }

        return objectStorageClient.getUrl(bucketName, fileName).toString();
    }

}
