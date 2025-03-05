package potenday.backend.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
class ScriptConverter implements AttributeConverter<List<Dialogue>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Dialogue> dialogues) {
        try {
            return objectMapper.writeValueAsString(dialogues);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }

    @Override
    public List<Dialogue> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Dialogue.class));
        } catch (IOException e) {
            throw new RuntimeException("JSON deserialization error", e);
        }
    }

}

