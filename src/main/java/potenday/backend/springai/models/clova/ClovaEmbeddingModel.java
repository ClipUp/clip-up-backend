package potenday.backend.springai.models.clova;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.ObservationRegistry;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.embedding.observation.DefaultEmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationDocumentation;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import potenday.backend.springai.models.clova.api.ClovaApi;
import potenday.backend.springai.models.clova.api.common.ClovaAiApiConstants;
import potenday.backend.springai.models.clova.api.common.ClovaApiClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ClovaEmbeddingModel implements EmbeddingModel {

    private static final EmbeddingModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultEmbeddingModelObservationConvention();

    private final ClovaEmbeddingOptions defaultOptions;
    private final RetryTemplate retryTemplate;
    private final ClovaApi clovaApi;
    private final MetadataMode metadataMode;
    private final ObservationRegistry observationRegistry;
    private EmbeddingModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    @Builder
    public ClovaEmbeddingModel(
        ClovaApi clovaApi, MetadataMode metadataMode, ClovaEmbeddingOptions defaultOptions,
        RetryTemplate retryTemplate, ObservationRegistry observationRegistry
    ) {
        Assert.notNull(clovaApi, "clovaApi must not be null");
        Assert.notNull(metadataMode, "metadataMode must not be null");
        Assert.notNull(defaultOptions, "defaultOptions must not be null");
        Assert.notNull(retryTemplate, "retryTemplate must not be null");
        Assert.notNull(observationRegistry, "observationRegistry must not be null");

        this.clovaApi = clovaApi;
        this.metadataMode = metadataMode;
        this.defaultOptions = defaultOptions;
        this.retryTemplate = setRetryTemplate(retryTemplate);
        this.observationRegistry = observationRegistry;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        ClovaEmbeddingOptions requestOptions = mergeOptions(request.getOptions(), this.defaultOptions);
        List<ClovaApi.EmbeddingRequest> apiRequests = createRequests(request, requestOptions);

        var observationContext = EmbeddingModelObservationContext.builder()
            .embeddingRequest(request)
            .provider(ClovaAiApiConstants.PROVIDER_NAME)
            .requestOptions(requestOptions)
            .build();

        return EmbeddingModelObservationDocumentation.EMBEDDING_MODEL_OPERATION
            .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
                this.observationRegistry)
            .observe(() -> {
                List<Embedding> embeddings = new ArrayList<>();
                List<EmbeddingResponseMetadata> metadataList = new ArrayList<>();

                for (int i = 0; i < apiRequests.size(); i++) {
                    var apiRequest = apiRequests.get(i);

                    ClovaApi.EmbeddingResponse.Embedding apiEmbeddingResponse = this.retryTemplate
                        .execute(ctx -> this.clovaApi.embeddings(apiRequest));

                    if (apiEmbeddingResponse == null) {
                        log.warn("No embeddings returned for request: {}", request);
                        continue;
                    }

                    var metadata = new EmbeddingResponseMetadata();
                    metadataList.add(metadata);

                    embeddings.add(new Embedding(apiEmbeddingResponse.embedding(), i));
                }

                EmbeddingResponse embeddingResponse = new EmbeddingResponse(embeddings, metadataList.isEmpty() ? null : metadataList.get(0));

                observationContext.setResponse(embeddingResponse);

                return embeddingResponse;
            });
    }

    @Override
    public float[] embed(Document document) {
        Assert.notNull(document, "Document must not be null");
        return this.embed(document.getFormattedContent(this.metadataMode));
    }

    public void setObservationConvention(EmbeddingModelObservationConvention observationConvention) {
        Assert.notNull(observationConvention, "observationConvention cannot be null");
        this.observationConvention = observationConvention;
    }

    private List<ClovaApi.EmbeddingRequest> createRequests(
        EmbeddingRequest request,
        ClovaEmbeddingOptions requestOptions
    ) {
        return request.getInstructions()
            .stream()
            .map(ClovaApi.EmbeddingRequest::new)
            .toList();
    }

    private ClovaEmbeddingOptions mergeOptions(
        @Nullable EmbeddingOptions runtimeOptions,
        ClovaEmbeddingOptions defaultOptions
    ) {
        var runtimeOptionsForProvider = ModelOptionsUtils.copyToTarget(runtimeOptions, EmbeddingOptions.class, ClovaEmbeddingOptions.class);

        if (runtimeOptionsForProvider == null) {
            return defaultOptions;
        }

        return ClovaEmbeddingOptions.builder()
            .model(ModelOptionsUtils.mergeOption(runtimeOptionsForProvider.getModel(), defaultOptions.getModel()))
            .dimensions(ModelOptionsUtils.mergeOption(runtimeOptionsForProvider.getDimensions(), defaultOptions.getDimensions()))
            .build();
    }

    private RetryTemplate setRetryTemplate(RetryTemplate retryTemplate) {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, Map.of(
            ClovaApiClientErrorException.class, true
        ));

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(60000);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

}
