package potenday.backend.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_NULL)
public record ApiResponse<T>(
    HttpStatus status,
    String message,
    T data
) {

    public static <T> ApiResponse<T> of(ApplicationException e) {
        return new ApiResponse<>(e.getHttpStatus(), e.getMessage(), null);
    }

    static <T> ApiResponse<T> of(ApplicationException e, T data) {
        return new ApiResponse<>(e.getHttpStatus(), e.getMessage(), data);
    }

    static <T> ApiResponse<T> of(HttpStatus status, T data) {
        return switch (status) {
            case HttpStatus.OK, HttpStatus.CREATED -> ApiResponse.of(status, "성공", data);
            default -> ApiResponse.of(status, "실패", data);
        };
    }

    static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

}
