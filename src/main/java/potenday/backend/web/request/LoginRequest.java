package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "이메일은 필수값입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수값입니다.")
    String password
) {

}
