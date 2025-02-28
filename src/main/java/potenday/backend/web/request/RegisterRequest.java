package potenday.backend.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수값입니다.")
    @Length(max = 100, message = "100자 이내입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    String password,

    @NotBlank(message = "이름은 필수값입니다.")
    @Length(min = 2, max = 10, message = "이름은 2~10자까지 입력할 수 있습니다.")
    String username
) {

}
