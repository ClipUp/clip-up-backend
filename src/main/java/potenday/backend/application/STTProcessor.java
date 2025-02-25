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
        audioFile = audioUtil.convertToMp3(audioFile);

        String fileName = String.format("%s.%s", id, extractExtension(audioFile));

        String audioFileUrl = fileUploader.upload(audioFile, BUCKET_NAME, fileName);
        int audioFileDuration = audioUtil.getDuration(audioFile);
//        List<Dialogue> script = sttConverter.convert(audioFileUrl);
        return new Result(audioFileUrl, audioFileDuration, List.of());
    }

    private String extractExtension(MultipartFile audioFile) {
        String originalFilename = audioFile.getOriginalFilename();

        Assert.notNull(originalFilename, "Original filename is null");

        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }

    record Result(
        String audioFileUrl,
        int audioFileDuration,
        List<Dialogue> script
    ) {

    }

}
