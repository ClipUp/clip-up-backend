package potenday.backend.infra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
    private static final long CONTINUATION_THRESHOLD = 3 * 1000;

    private final RestClient restClient;

    ClovaSpeech(
        @Value("${spring.ai.clova.speech.invoke-url}")
        String invokeUrl,
        @Value("${spring.ai.clova.speech.secret-key}") String secretKey,
        RestClient.Builder restClientBuilder
    ) {
        Consumer<HttpHeaders> finalHeaders = h -> {
            h.set("X-CLOVASPEECH-API-KEY", secretKey);
            h.setContentType(MediaType.APPLICATION_JSON);
        };
        this.restClient = restClientBuilder.baseUrl(invokeUrl)
            .defaultHeaders(finalHeaders)
            .build();
    }

    @Override
    public List<Dialogue> convert(String fileUrl) {
        ResponseEntity<ClovaSTTResponse> response = this.restClient.post()
            .uri(STT_URL)
            .body(ClovaSTTRequest.of(fileUrl.split(DEFAULT_BUCKET_NAME)[1]))
            .retrieve().toEntity(ClovaSTTResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw ErrorCode.INTERNAL_SERVER_ERROR.toException();
        }

        return convertToScript(response.getBody().segments);
    }

    private List<Dialogue> convertToScript(List<ClovaSTTResponse.Segment> segments) {
        List<Dialogue> dialogues = new ArrayList<>();
        ClovaSTTResponse.Segment lastSegment = null;
        for (ClovaSTTResponse.Segment segment : segments) {
            if (lastSegment == null) {
                lastSegment = segment;
            } else if (isContinuation(lastSegment, segment)) {
                lastSegment.end = segment.end;
                lastSegment.text += " " + segment.text;
            } else {
                dialogues.add(lastSegment.toDialogue());
                lastSegment = null;
            }
        }
        return dialogues;
    }

    private boolean isContinuation(ClovaSTTResponse.Segment lastSegment, ClovaSTTResponse.Segment currentSegment) {
        return lastSegment.diarization.label.equals(currentSegment.diarization.label) && currentSegment.start - lastSegment.end <= CONTINUATION_THRESHOLD;
    }

    @JsonInclude(Include.NON_NULL)
    private record ClovaSTTRequest(
        @JsonProperty("dataKey") String dataKey,
        @JsonProperty("language") String language,
        @JsonProperty("completion") String completion
    ) {

        static ClovaSTTRequest of(String fileName) {
            return new ClovaSTTRequest(fileName, "ko-KR", "sync");
        }

    }

    @JsonInclude(Include.NON_NULL)
    private record ClovaSTTResponse(
        @JsonProperty("segments") List<Segment> segments
    ) {

        @JsonInclude(Include.NON_NULL)
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

            @JsonInclude(Include.NON_NULL)
            static class Diarization {

                @JsonProperty("label")
                String label;

            }

        }

    }


}
