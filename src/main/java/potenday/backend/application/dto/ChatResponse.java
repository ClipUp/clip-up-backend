package potenday.backend.application.dto;

public record ChatResponse(
    String answer,
    String sessionId
) {

    public static ChatResponse of(String answer, String sessionId) {
        return new ChatResponse(answer, sessionId);
    }

}
