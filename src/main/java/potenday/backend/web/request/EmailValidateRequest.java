package potenday.backend.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EmailValidateRequest(
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수값입니다.")
    @Length(max = 100, message = "100자 이내입니다.")
    String email,

    @NotBlank(message = "코드는 필수값입니다.")
    @Length(min = 6, max = 6, message = "6자입니다.")
    String code
) {

}
