package potenday.backend.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
    @Email(message = "이메일 양식이어야 합니다.")
    @NotBlank(message = "이메일은 필수값입니다.")
    @Length(max = 100, message = "100자 이내입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수값입니다.")
    String password,

    @NotBlank(message = "사용자명은 필수값입니다.")
    @Length(max = 50, message = "50자 이내입니다.")
    String username
) {

}
