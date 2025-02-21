package potenday.backend.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserUpdateRequest(
    @Email
    @NotBlank
    @Length(max = 100)
    String email,

    @NotBlank
    @Length(max = 50)
    String username
) {

}
