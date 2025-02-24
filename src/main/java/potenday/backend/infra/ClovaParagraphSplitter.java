package potenday.backend.infra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import potenday.backend.application.ParagraphSplitter;
import potenday.backend.springai.models.clova.api.ClovaApi;
import potenday.backend.springai.models.clova.api.common.ClovaApiClientErrorException;

import java.util.List;
import java.util.function.Consumer;

class ClovaParagraphSplitter implements ParagraphSplitter {

    private static final String PARAGRAPH_SPLIT_URL = "/v1/api-tools/segmentation";

    private final RestClient restClient;

    @Builder
    ClovaParagraphSplitter(
        String baseUrl,
        ApiKey apiKey,
        RestClient.Builder restClientBuilder,
        ResponseErrorHandler responseErrorHandler
    ) {

        Consumer<HttpHeaders> finalHeaders = h -> {
            if (!(apiKey instanceof NoopApiKey)) {
                h.setBearerAuth(apiKey.getValue());
            }

            h.setContentType(MediaType.APPLICATION_JSON);
        };
        this.restClient = restClientBuilder.baseUrl(baseUrl)
            .defaultHeaders(finalHeaders)
            .defaultStatusHandler(responseErrorHandler)
            .build();
    }

    @Override
    public List<String> apply(String text) {
        ResponseEntity<ParagraphSplitResponse> paragraphSplitResponse = this.restClient.post()
            .uri(PARAGRAPH_SPLIT_URL)
            .body(new ParagraphSplitRequest(text))
            .retrieve()
            .toEntity(ParagraphSplitResponse.class);

        if (!paragraphSplitResponse.getStatusCode()
            .is2xxSuccessful()) {
            throw new ClovaApiClientErrorException("Network Error");
        }

        if (!(paragraphSplitResponse.getBody() != null && paragraphSplitResponse.getBody()
            .status()
            .code()
            .equals("20000"))) {
            throw new ClovaApiClientErrorException(paragraphSplitResponse.getBody().status().toString());
        }

        return paragraphSplitResponse.getBody().result.topicSeg.stream().map(topic -> {
            StringBuilder topicText = new StringBuilder();
            for (String s : topic) {
                topicText.append(s).append("\n");
            }
            return topicText.toString();
        }).toList();
    }

    @JsonInclude(Include.NON_NULL)
    private record ParagraphSplitRequest(
        @JsonProperty("text") String text
    ) {

    }

    @JsonInclude(Include.NON_NULL)
    private record ParagraphSplitResponse(
        @JsonProperty("status") ClovaApi.Status status,
        @JsonProperty("result") Result result
    ) {

        @JsonInclude(Include.NON_NULL)
        record Result(
            @JsonProperty("topicSeg") List<List<String>> topicSeg
        ) {

        }

    }

}
