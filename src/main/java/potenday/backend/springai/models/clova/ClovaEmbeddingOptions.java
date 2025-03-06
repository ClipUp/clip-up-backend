package potenday.backend.springai.models.clova;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Builder.Default;
import org.springframework.ai.embedding.EmbeddingOptions;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClovaEmbeddingOptions implements EmbeddingOptions {

    @JsonProperty("model")
    private String model;
    @JsonProperty("dimensions")
    @Default
    private Integer dimensions = 0;

}
