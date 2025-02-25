package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;

public record MeetingUpdateRequest(
    @NotBlank(message = "제목을 필수값입니다.")
    String title
) {

}
