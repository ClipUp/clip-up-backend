package potenday.backend.infra.ai.clova.autoconfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class ClovaParentProperties {

    private String apiKey;
    private String appName;
    private String baseUrl;

}
