package potenday.backend.springai.models.clova;

import io.micrometer.observation.ObservationRegistry;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.observation.ChatModelObservationDocumentation;
import org.springframework.ai.chat.observation.DefaultChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import potenday.backend.springai.models.clova.api.ClovaApi;
import potenday.backend.springai.models.clova.api.ClovaApi.ChatCompletion;
import potenday.backend.springai.models.clova.api.ClovaApi.ChatCompletionMessage;
import potenday.backend.springai.models.clova.api.ClovaApi.ChatCompletionRequest;
import potenday.backend.springai.models.clova.api.common.ClovaAiApiConstants;

import java.util.List;
import java.util.Map;

@Slf4j
public class ClovaChatModel implements ChatModel {

    private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultChatModelObservationConvention();

    private final ClovaChatOptions defaultOptions;
    private final RetryTemplate retryTemplate;
    private final ClovaApi clovaApi;
    private final ObservationRegistry observationRegistry;

    private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    @Builder
    public ClovaChatModel(
        ClovaChatOptions defaultOptions,
        RetryTemplate retryTemplate,
        ClovaApi clovaApi,
        ObservationRegistry observationRegistry
    ) {
        this.defaultOptions = defaultOptions;
        this.retryTemplate = retryTemplate;
        this.clovaApi = clovaApi;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        Prompt requestPrompt = buildRequestPrompt(prompt);
        return internalCall(requestPrompt);
    }

    public void setObservationConvention(ChatModelObservationConvention observationConvention) {
        Assert.notNull(observationConvention, "observationConvention cannot be null");
        this.observationConvention = observationConvention;
    }

    private ChatResponse internalCall(Prompt prompt) {
        ChatCompletionRequest request = createRequest(prompt);

        ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
            .prompt(prompt)
            .provider(ClovaAiApiConstants.PROVIDER_NAME)
            .requestOptions(prompt.getOptions())
            .build();

        return ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
            .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
                this.observationRegistry)
            .observe(() -> {

                ChatCompletion chatCompletion = this.retryTemplate
                    .execute(ctx -> this.clovaApi.chatCompletionEntity(request));

                if (chatCompletion == null) {
                    log.warn("No chat completion returned for prompt: {}", prompt);
                    return new ChatResponse(List.of());
                }

                ChatCompletionMessage message = chatCompletion.message();
                if (message == null) {
                    log.warn("No message returned for prompt: {}", prompt);
                    return new ChatResponse(List.of());
                }

                Map<String, Object> metadata = Map.of("role", message.role() != null ? message.role().name() : "");
                Generation generation = buildGeneration(chatCompletion, metadata);

                ChatResponse chatResponse = new ChatResponse(List.of(generation));

                observationContext.setResponse(chatResponse);

                return chatResponse;

            });
    }

    private Generation buildGeneration(ChatCompletion chatCompletion, Map<String, Object> metadata) {
        String stopReason = (chatCompletion.stopReason() != null ? chatCompletion.stopReason().name() : "");
        var generationMetadataBuilder = ChatGenerationMetadata.builder().finishReason(stopReason);

        String textContent = chatCompletion.message().content();

        var assistantMessage = new AssistantMessage(textContent, metadata);
        return new Generation(assistantMessage, generationMetadataBuilder.build());
    }

    private ChatCompletionRequest createRequest(Prompt prompt) {
        List<ChatCompletionMessage> chatCompletionMessages = prompt.getInstructions().stream().map(message -> {
            if (message.getMessageType() == MessageType.USER || message.getMessageType() == MessageType.SYSTEM || message.getMessageType() == MessageType.ASSISTANT) {
                return List.of(new ChatCompletionMessage(ChatCompletionMessage.Role.valueOf(message.getMessageType()
                    .name()), message.getText()));
            } else {
                throw new IllegalArgumentException("Unsupported message type: " + message.getMessageType());
            }
        }).flatMap(List::stream).toList();

        ChatCompletionRequest request = ChatCompletionRequest.of(chatCompletionMessages);

        return ModelOptionsUtils.merge(prompt.getOptions(), request, ChatCompletionRequest.class);
    }

    private Prompt buildRequestPrompt(Prompt prompt) {
        ClovaChatOptions runtimeOptions = null;
        if (prompt.getOptions() != null) {
            runtimeOptions = ModelOptionsUtils.copyToTarget(prompt.getOptions(), ChatOptions.class, ClovaChatOptions.class);
        }

        ClovaChatOptions requestOptions = ModelOptionsUtils.merge(runtimeOptions, this.defaultOptions, ClovaChatOptions.class);

        return new Prompt(prompt.getInstructions(), requestOptions);
    }

}
