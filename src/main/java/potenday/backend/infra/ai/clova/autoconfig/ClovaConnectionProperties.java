package potenday.backend.infra.ai.clova.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ClovaConnectionProperties.CONFIG_PREFIX)
class ClovaConnectionProperties extends ClovaParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.clova";
    public static final String DEFAULT_BASE_URL = "https://clovastudio.stream.ntruss.com/";

    public ClovaConnectionProperties() {
        super.setBaseUrl(DEFAULT_BASE_URL);
    }

}
