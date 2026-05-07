package hu.szbz.das.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNKNOWN_CALENDAR_OWNER(HttpStatus.FORBIDDEN),
    CALENDER_COLLISION(HttpStatus.CONFLICT),
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    CLOSED_ACTIVITY(HttpStatus.BAD_REQUEST),
    ;

    private final HttpStatusCode httpCode;

    ErrorCode(HttpStatusCode httpCode) {
        this.httpCode = httpCode;
    }

    public HttpStatusCode getHttpCode() {
        return httpCode;
    }
}
