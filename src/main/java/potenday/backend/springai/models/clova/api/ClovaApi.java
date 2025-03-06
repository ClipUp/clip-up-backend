package potenday.backend.springai.models.clova.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import potenday.backend.springai.models.clova.api.ClovaApi.ChatCompletion.ChatCompletionMessage;
import potenday.backend.springai.models.clova.api.ClovaApi.EmbeddingResponse.Embedding;
import potenday.backend.springai.models.clova.api.common.ClovaApiClientErrorException;

import java.util.List;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ClovaApi {

    private final String completionsPath;
    private final String embeddingsPath;
    private final RestClient restClient;

    @Builder
    public ClovaApi(
        String baseUrl,
        ApiKey apiKey,
        String completionsPath,
        String embeddingsPath,
        RestClient.Builder restClientBuilder,
        ResponseErrorHandler responseErrorHandler
    ) {
        Assert.hasText(completionsPath, "Completions Path must not be null");
        Assert.hasText(embeddingsPath, "Embedding Path must not be null");

        this.completionsPath = completionsPath;
        this.embeddingsPath = embeddingsPath;

        Consumer<HttpHeaders> finalHeaders = h -> {
            if (!(apiKey instanceof NoopApiKey)) {
                h.setBearerAuth(apiKey.getValue());
            }

            h.setContentType(MediaType.APPLICATION_JSON);
        };
        this.restClient = restClientBuilder
            .baseUrl(baseUrl)
            .defaultHeaders(finalHeaders)
            .defaultStatusHandler(responseErrorHandler)
            .build();
    }

    public ChatCompletion chatCompletion(ChatCompletionRequest chatRequest) {
        Assert.notNull(chatRequest, "The request body can not be null.");

        ResponseEntity<ChatCompletionResponse> chatResponse = this.restClient.post()
            .uri(this.completionsPath + chatRequest.model)
            .body(chatRequest)
            .retrieve()
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                (request, response) -> {
                    throw new ClovaApiClientErrorException("429 Too Many Requests");
                }
            )

            .toEntity(ChatCompletionResponse.class);

        if (!chatResponse.getStatusCode().is2xxSuccessful() || chatResponse.getBody() == null) {
            throw new RuntimeException("Network Error");
        }

        return chatResponse.getBody().result;
    }

    public Embedding embeddings(EmbeddingRequest embeddingRequest) {
        Assert.notNull(embeddingRequest, "The request body can not be null.");
        Assert.notNull(embeddingRequest.text(), "The input text can not be null.");

        ResponseEntity<EmbeddingResponse> embeddingResponse = this.restClient.post()
            .uri(this.embeddingsPath)
            .body(embeddingRequest)
            .retrieve()
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                (request, response) -> {
                    throw new ClovaApiClientErrorException("429 Too Many Requests");
                }
            )
            .toEntity(EmbeddingResponse.class);

        if (!embeddingResponse.getStatusCode().is2xxSuccessful() || embeddingResponse.getBody() == null) {
            throw new RuntimeException("Network Error");
        }

        return embeddingResponse.getBody().result;
    }

    @JsonInclude(Include.NON_NULL)
    public record ChatCompletion(
        @JsonProperty("message") ChatCompletionMessage message,
        @JsonProperty("stopReason") ChatCompletionFinishReason stopReason,
        @JsonProperty("inputLength") Integer inputLength,
        @JsonProperty("outputLength") Integer outputLength,
        @JsonProperty("seed") Long seed
    ) {

        public enum ChatCompletionFinishReason {
            @JsonProperty("length")
            LENGTH,

            @JsonProperty("end_token")
            END_TOKEN,

            @JsonProperty("stop_before")
            STOP_BEFORE
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

    @JsonInclude(Include.NON_NULL)
    public record ChatCompletionResponse(
        @JsonProperty("status") Status status,
        @JsonProperty("result") ChatCompletion result
    ) {

    }

    @JsonInclude(Include.NON_NULL)
    public record EmbeddingRequest(
        @JsonProperty("text") String text
    ) {

    }

    @JsonInclude(Include.NON_NULL)
    public record EmbeddingResponse(
        @JsonProperty("status") Status status,
        @JsonProperty("result") Embedding result
    ) {

        @JsonInclude(Include.NON_NULL)
        public record Embedding(
            @JsonProperty("embedding") float[] embedding,
            @JsonProperty("inputToken") Integer inputTokens) {

        }

    }

}
