package potenday.backend.support;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;

    ApplicationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
