package potenday.backend.springai.autoconfig.clova;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.retry.SpringAiRetryAutoConfiguration;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import potenday.backend.springai.models.clova.ClovaChatModel;
import potenday.backend.springai.models.clova.ClovaEmbeddingModel;
import potenday.backend.springai.models.clova.api.ClovaApi;

@AutoConfiguration(after = {RestClientAutoConfiguration.class, SpringAiRetryAutoConfiguration.class})
@ConditionalOnClass(ClovaApi.class)
@EnableConfigurationProperties({ClovaConnectionProperties.class, ClovaChatProperties.class, ClovaEmbeddingProperties.class})
@ImportAutoConfiguration(classes = {SpringAiRetryAutoConfiguration.class, RestClientAutoConfiguration.class})
class ClovaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = ClovaChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public ClovaChatModel clovaChatModel(
        ClovaConnectionProperties connectionProperties,
        ClovaChatProperties chatProperties,
        ObjectProvider<RestClient.Builder> restClientBuilderProvider,
        RetryTemplate retryTemplate,
        ResponseErrorHandler responseErrorHandler,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<ChatModelObservationConvention> observationConvention
    ) {
        var clovaApi = clovaApi(chatProperties, connectionProperties, restClientBuilderProvider.getIfAvailable(RestClient::builder), responseErrorHandler, "chat");

        var chatModel = ClovaChatModel.builder()
            .clovaApi(clovaApi)
            .defaultOptions(chatProperties.getOptions())
            .retryTemplate(retryTemplate)
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        observationConvention.ifAvailable(chatModel::setObservationConvention);

        return chatModel;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = ClovaEmbeddingProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public ClovaEmbeddingModel clovaEmbeddingModel(
        ClovaConnectionProperties connectionProperties,
        ClovaEmbeddingProperties embeddingProperties,
        ObjectProvider<RestClient.Builder> restClientBuilderProvider,
        RetryTemplate retryTemplate,
        ResponseErrorHandler responseErrorHandler,
        ObjectProvider<ObservationRegistry> observationRegistry,
        ObjectProvider<EmbeddingModelObservationConvention> observationConvention
    ) {
        var clovaApi = clovaApi(embeddingProperties, connectionProperties, restClientBuilderProvider.getIfAvailable(RestClient::builder), responseErrorHandler, "embedding");

        var embeddingModel = ClovaEmbeddingModel.builder()
            .clovaApi(clovaApi)
            .defaultOptions(embeddingProperties.getOptions())
            .metadataMode(embeddingProperties.getMetadataMode())
            .retryTemplate(retryTemplate)
            .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
            .build();

        observationConvention.ifAvailable(embeddingModel::setObservationConvention);

        return embeddingModel;
    }

    private ClovaApi clovaApi(
        ClovaParentProperties properties,
        ClovaConnectionProperties commonProperties,
        RestClient.Builder restClientBuilder,
        ResponseErrorHandler responseErrorHandler,
        String modelType
    ) {
        ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, properties, modelType);

        return ClovaApi.builder()
            .baseUrl(resolved.baseUrl() + resolved.appName())
            .apiKey(new SimpleApiKey(resolved.apiKey()))
            .completionsPath(ClovaChatProperties.DEFAULT_COMPLETIONS_PATH)
            .embeddingsPath(ClovaEmbeddingProperties.DEFAULT_EMBEDDING_PATH)
            .restClientBuilder(restClientBuilder)
            .responseErrorHandler(responseErrorHandler)
            .build();
    }

    private ResolvedConnectionProperties resolveConnectionProperties(
        ClovaParentProperties connectionProperties,
        ClovaParentProperties modelProperties,
        String modelType
    ) {

        String baseUrl = !StringUtils.hasText(modelProperties.getBaseUrl()) ? connectionProperties.getBaseUrl() : modelProperties.getBaseUrl();
        String apiKey = !StringUtils.hasText(modelProperties.getApiKey()) ? connectionProperties.getApiKey() : modelProperties.getApiKey();
        String appName = !StringUtils.hasText(modelProperties.getAppName()) ? connectionProperties.getAppName() : modelProperties.getAppName();

        Assert.hasText(baseUrl,
            "Clova base URL must be set.  Use the connection property: spring.ai.clova.base-url or spring.ai.clova."
                + modelType + ".base-url property.");
        Assert.hasText(apiKey,
            "Clova API key must be set. Use the connection property: spring.ai.clova.api-key or spring.ai.clova."
                + modelType + ".api-key property.");

        return new ResolvedConnectionProperties(baseUrl, apiKey, appName);
    }

    private record ResolvedConnectionProperties(String baseUrl, String apiKey, String appName) {

    }

}
