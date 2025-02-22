package potenday.backend.infra.ai.clova.model.api.common;

public class ClovaApiClientErrorException extends RuntimeException {

    public ClovaApiClientErrorException(String message) {
        super(message);
    }

    public ClovaApiClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
