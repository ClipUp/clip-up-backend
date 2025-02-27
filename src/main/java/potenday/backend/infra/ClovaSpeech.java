package potenday.backend.infra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import potenday.backend.application.STTConverter;
import potenday.backend.domain.Dialogue;
import potenday.backend.support.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
class ClovaSpeech implements STTConverter {

    private static final String STT_URL = "/recognizer/object-storage";
    private static final String DEFAULT_BUCKET_NAME = "clip-up/";
    private static final long CONTINUATION_THRESHOLD = 500;

    private final RestClient restClient;

    ClovaSpeech(
        @Value("${spring.ai.clova.speech.invoke-url}") String invokeUrl,
        @Value("${spring.ai.clova.speech.secret-key}") String secretKey,
        RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder.baseUrl(invokeUrl)
            .defaultHeaders(createHeaders(secretKey))
            .build();
    }

    @Override
    public List<Dialogue> convert(String fileUrl) {
        String fileName = extractFileName(fileUrl);
        ClovaSTTRequest request = new ClovaSTTRequest(fileName, "ko-KR", "sync");

        ResponseEntity<ClovaSTTResponse> response = this.restClient.post()
            .uri(STT_URL)
            .body(request)
            .retrieve()
            .toEntity(ClovaSTTResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw ErrorCode.INTERNAL_SERVER_ERROR.toException();
        }

        return convertToDialogues(response.getBody().segments);
    }

    private List<Dialogue> convertToDialogues(List<ClovaSTTResponse.Segment> segments) {
        List<Dialogue> dialogues = new ArrayList<>();
        if (segments.isEmpty()) return dialogues;

        ClovaSTTResponse.Segment lastSegment = segments.get(0);

        for (int i = 1; i < segments.size(); i++) {
            ClovaSTTResponse.Segment currentSegment = segments.get(i);

            if (isContinuation(lastSegment, currentSegment)) {
                lastSegment.mergeWith(currentSegment);
            } else {
                dialogues.add(lastSegment.toDialogue());
                lastSegment = currentSegment;
            }
        }

        dialogues.add(lastSegment.toDialogue()); // 마지막 대화 추가
        return dialogues;
    }

    private boolean isContinuation(ClovaSTTResponse.Segment last, ClovaSTTResponse.Segment current) {
        return last.hasSameSpeaker(current) && current.start - last.end <= CONTINUATION_THRESHOLD;
    }

    private String extractFileName(String fileUrl) {
        if (!fileUrl.contains(DEFAULT_BUCKET_NAME)) {
            throw new IllegalArgumentException("Invalid file URL format: " + fileUrl);
        }
        return fileUrl.split(DEFAULT_BUCKET_NAME)[1];
    }

    private Consumer<HttpHeaders> createHeaders(String secretKey) {
        return headers -> {
            headers.set("X-CLOVASPEECH-API-KEY", secretKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record ClovaSTTRequest(
        @JsonProperty("dataKey") String dataKey,
        @JsonProperty("language") String language,
        @JsonProperty("completion") String completion
    ) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record ClovaSTTResponse(@JsonProperty("segments") List<Segment> segments) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        static class Segment {

            @JsonProperty("start")
            Long start;
            @JsonProperty("end")
            Long end;
            @JsonProperty("text")
            String text;
            @JsonProperty("diarization")
            Diarization diarization;

            Dialogue toDialogue() {
                return Dialogue.create(diarization.label, start, end, text);
            }

            boolean hasSameSpeaker(Segment other) {
                return diarization.label.equals(other.diarization.label);
            }

            void mergeWith(Segment other) {
                this.end = other.end;
                this.text += " " + other.text;
            }

            @JsonInclude(JsonInclude.Include.NON_NULL)
            static class Diarization {

                @JsonProperty("label")
                String label;

            }

        }

    }

}
