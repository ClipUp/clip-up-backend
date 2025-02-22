package potenday.backend.infra.ai.clova.autoconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import potenday.backend.infra.ai.clova.model.ClovaChatOptions;

@Getter
@Setter
@ConfigurationProperties(ClovaChatProperties.CONFIG_PREFIX)
class ClovaChatProperties extends ClovaParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.clova.chat";
    public static final String DEFAULT_CHAT_MODEL = "HCX-003";
    public static final String DEFAULT_COMPLETIONS_PATH = "/v1/chat-completions/";
    private static final Double DEFAULT_TEMPERATURE = 0.8;

    private boolean enabled = true;

    private String completionsPath = DEFAULT_COMPLETIONS_PATH;

    @NestedConfigurationProperty
    private ClovaChatOptions options = ClovaChatOptions.builder()
        .model(DEFAULT_CHAT_MODEL)
        .temperature(DEFAULT_TEMPERATURE)
        .build();

}
