package potenday.backend.web.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(
    @NotBlank
    String originalPassword,

    @NotBlank
    String newPassword
) {

}
