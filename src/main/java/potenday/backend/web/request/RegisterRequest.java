package potenday.backend.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
    @Email
    @NotBlank
    @Length(max = 100)
    String email,

    @NotBlank
    String password,

    @NotBlank
    @Length(max = 50)
    String username
) {

}
