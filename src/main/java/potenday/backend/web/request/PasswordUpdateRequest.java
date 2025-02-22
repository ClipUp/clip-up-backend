package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(
    @NotBlank(message = "기존 비밀번호는 필수값입니다.")
    String originalPassword,

    @NotBlank(message = "새로운 비밀번호는 필수값입니다.")
    String newPassword
) {

}
