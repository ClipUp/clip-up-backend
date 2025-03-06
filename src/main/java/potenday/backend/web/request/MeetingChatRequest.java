package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;

public record MeetingChatRequest(
    @NotBlank(message = "질문은 필수값입니다.")
    String question,
    
    String sessionId
) {

}
