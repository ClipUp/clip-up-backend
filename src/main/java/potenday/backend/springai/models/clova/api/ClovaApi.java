package potenday.backend.springai.models.clova.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import potenday.backend.springai.models.clova.api.common.ClovaApiClientErrorException;

import java.util.List;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ClovaApi {

    private final String completionsPath;
    private final RestClient restClient;

    @Builder
    public ClovaApi(
        String baseUrl,
        ApiKey apiKey,
        String completionsPath,
        RestClient.Builder restClientBuilder,
        ResponseErrorHandler responseErrorHandler
    ) {
        Assert.hasText(completionsPath, "Completions Path must not be null");

        this.completionsPath = completionsPath;

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

    public ChatCompletion chatCompletionEntity(ChatCompletionRequest chatRequest) {
        Assert.notNull(chatRequest, "The request body can not be null.");

        ResponseEntity<ChatCompletionResponse> chatResponse = this.restClient.post()
            .uri(this.completionsPath + chatRequest.model)
            .body(chatRequest)
            .retrieve()
            .toEntity(ChatCompletionResponse.class);

        if (!chatResponse.getStatusCode()
            .is2xxSuccessful()) {
            throw new ClovaApiClientErrorException("Network Error");
        }

        if (!(chatResponse.getBody() != null && chatResponse.getBody().status.code.equals("20000"))) {
            throw new ClovaApiClientErrorException(chatResponse.getBody().status.toString());
        }

        return chatResponse.getBody().result;
    }

    public enum ChatCompletionFinishReason {
        @JsonProperty("length")
        LENGTH,

        @JsonProperty("end_token")
        END_TOKEN,

        @JsonProperty("stop_before")
        STOP_BEFORE
    }

    @JsonInclude(Include.NON_NULL)
    public record ChatCompletionResponse(
        @JsonProperty("status") Status status,
        @JsonProperty("result") ChatCompletion result
    ) {

    }

    @JsonInclude(Include.NON_NULL)
    public record Status(
        @JsonProperty("code") String code,
        @JsonProperty("message") String message
    ) {

        @Override
        public String toString() {
            return String.format("code: %s, message: %s", code, message);
        }

    }

    @JsonInclude(Include.NON_NULL)
    public record ChatCompletion(
        @JsonProperty("message") ChatCompletionMessage message,
        @JsonProperty("stopReason") ChatCompletionFinishReason stopReason,
        @JsonProperty("inputLength") Integer inputLength,
        @JsonProperty("outputLength") Integer outputLength,
        @JsonProperty("seed") Integer seed
    ) {

    }

    @JsonInclude(Include.NON_NULL)
    public record AiFilter(
        @JsonProperty("groupName") String groupName,
        @JsonProperty("name") String name,
        @JsonProperty("score") String score,
        @JsonProperty("result") String result,
        @JsonProperty("aiFilter") List<AiFilter> aiFilter
    ) {

    }

    @JsonInclude(Include.NON_NULL)
    public record ChatCompletionRequest(
        @JsonProperty("model") String model,
        @JsonProperty("messages") List<ChatCompletionMessage> messages,
        @JsonProperty("temperature") Double temperature,
        @JsonProperty("topK") Integer topK,
        @JsonProperty("topP") Double topP,
        @JsonProperty("repeatPenalty") Double repeatPenalty,
        @JsonProperty("stopBefore") List<String> stopBefore,
        @JsonProperty("maxTokens") Integer maxTokens,
        @JsonProperty("includeAiFilters") Boolean includeAiFilters,
        @JsonProperty("seed") Integer seed
    ) {

        public static ChatCompletionRequest of(List<ChatCompletionMessage> messages) {
            return new ChatCompletionRequest(null, messages, null, null, null, null, null, null, null, null);
        }

    }

    public record ChatCompletionMessage(
        @JsonProperty("role") Role role,
        @JsonProperty("content") String content
    ) {

        public enum Role {
            @JsonProperty("system")
            SYSTEM,

            @JsonProperty("user")
            USER,

            @JsonProperty("assistant")
            ASSISTANT,
        }

    }

}
