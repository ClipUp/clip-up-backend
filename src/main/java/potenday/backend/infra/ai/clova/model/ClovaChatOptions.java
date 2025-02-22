package potenday.backend.infra.ai.clova.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Builder.Default;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ClovaChatOptions implements ChatOptions {

    @JsonProperty("model")
    private String model;
    @JsonProperty("temperature")
    private Double temperature;
    @JsonProperty("topK")
    private Integer topK;
    @JsonProperty("topP")
    private Double topP;
    @JsonProperty("maxTokens")
    private Integer maxTokens;

    @JsonProperty("repeatPenalty")
    private Double repeatPenalty;
    @JsonProperty("stopBefore")
    @Default
    private List<String> stopBefore = new ArrayList<>();
    @JsonProperty("includeAiFilters")
    private Boolean includeAiFilters;
    @JsonProperty("seed")
    private Integer seed;

    @Override
    public Double getFrequencyPenalty() {
        return 0.0;
    }

    @Override
    public Double getPresencePenalty() {
        return 0.0;
    }

    @Override
    public List<String> getStopSequences() {
        return getStopBefore();
    }

    @Override
    public ClovaChatOptions copy() {
        return ClovaChatOptions.builder()
            .model(this.model)
            .temperature(this.temperature)
            .topK(this.topK)
            .topP(this.topP)
            .maxTokens(this.maxTokens)
            .repeatPenalty(this.repeatPenalty)
            .stopBefore(this.stopBefore)
            .includeAiFilters(this.includeAiFilters)
            .seed(this.seed)
            .build();
    }

}
