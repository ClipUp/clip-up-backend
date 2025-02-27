package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.domain.Dialogue;

import java.util.List;

@RequiredArgsConstructor
@Component
class STTProcessor {

    private static final String BUCKET_NAME = "meeting-audio";

    private final AudioUtil audioUtil;
    private final FileUploader fileUploader;
    private final STTConverter sttConverter;

    Result convert(String id, MultipartFile audioFile) {
        AudioUtil.Result result = audioUtil.convertToMp3(audioFile);
        String audioFileUrl = uploadFile(id, result.mp3File());
        List<Dialogue> script = sttConverter.convert(audioFileUrl);

        return new Result(audioFileUrl, result.fileDuration(), script);
    }

    private String uploadFile(String id, MultipartFile audioFile) {
        String extension = extractExtension(audioFile);
        String fileName = String.format("%s.%s", id, extension);
        return fileUploader.upload(audioFile, BUCKET_NAME, fileName);
    }

    private String extractExtension(MultipartFile audioFile) {
        String originalFilename = audioFile.getOriginalFilename();
        Assert.notNull(originalFilename, "Original filename is null");

        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }

    record Result(String audioFileUrl, int audioFileDuration, List<Dialogue> script) {

    }

}
