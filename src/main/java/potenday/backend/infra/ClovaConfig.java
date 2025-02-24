package potenday.backend.infra;

import org.springframework.ai.model.SimpleApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import potenday.backend.application.ParagraphSplitter;

@Configuration
class ClovaConfig {

    @Value("${spring.ai.clova.api-key}")
    private String apiKey;

    @Value("${spring.ai.clova.base-url}")
    private String baseUrl;

    @Value("${spring.ai.clova.paragraph-split.app-name}")
    private String appName;

    @Bean
    ParagraphSplitter paragraphSplitter(
        RestClient.Builder restClientBuilder,
        ResponseErrorHandler responseErrorHandler
    ) {
        return ClovaParagraphSplitter.builder()
            .baseUrl(baseUrl + appName)
            .apiKey(new SimpleApiKey(apiKey))
            .restClientBuilder(restClientBuilder)
            .responseErrorHandler(responseErrorHandler)
            .build();

    }

}
