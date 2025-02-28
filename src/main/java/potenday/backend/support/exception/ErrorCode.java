package potenday.backend.support.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    WRONG_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),

    // User
    ALREADY_USED_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUNDED(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),

    // Meeting
    MEETING_NOT_FOUNDED(HttpStatus.NOT_FOUND, "존재하지 않는 회의입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public ApplicationException toException() {
        return new ApplicationException(this.httpStatus, this.message);
    }
}
