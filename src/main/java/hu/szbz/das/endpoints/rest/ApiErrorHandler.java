package hu.szbz.das.endpoints.rest;

import hu.szbz.das.api.Error;
import hu.szbz.das.errors.DailyActivitiesException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(DailyActivitiesException.class)
    public ResponseEntity<Error> handleDailyActivitiesException(DailyActivitiesException e) {
        Error dto = new Error();
        dto.setCode(e.getErrorCode().name());
        dto.setMessage(e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpCode()).body(dto);
    }
}
