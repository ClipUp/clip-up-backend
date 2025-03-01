package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record MeetingUpdateRequest(
    @NotBlank(message = "제목을 필수값입니다.")
    @Length(min = 2, max = 10, message = "제목은 2~40자까지 입력할 수 있습니다.")
    String title
) {

}
