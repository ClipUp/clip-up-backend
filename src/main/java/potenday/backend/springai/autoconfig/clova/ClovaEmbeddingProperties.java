package potenday.backend.springai.autoconfig.clova;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.document.MetadataMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import potenday.backend.springai.models.clova.ClovaEmbeddingOptions;

@Getter
@Setter
@ConfigurationProperties(ClovaEmbeddingProperties.CONFIG_PREFIX)
class ClovaEmbeddingProperties extends ClovaParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.clova.embedding";
    public static final String DEFAULT_EMBEDDING_PATH = "/v1/api-tools/embedding/v2";

    private boolean enabled = true;

    private String completionsPath = DEFAULT_EMBEDDING_PATH;
    private MetadataMode metadataMode = MetadataMode.EMBED;

    @NestedConfigurationProperty
    private ClovaEmbeddingOptions options = ClovaEmbeddingOptions.builder()
        .build();

}
