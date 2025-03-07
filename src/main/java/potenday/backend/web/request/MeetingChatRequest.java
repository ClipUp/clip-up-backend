package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record MeetingChatRequest(
    @NotBlank(message = "질문은 필수값입니다.")
    @Length(max = 500, message = "질문은 500자 이하입니다.")
    String question,

    String sessionId
) {

}
