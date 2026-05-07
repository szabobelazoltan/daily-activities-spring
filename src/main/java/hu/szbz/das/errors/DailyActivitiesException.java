package hu.szbz.das.errors;

public class DailyActivitiesException extends RuntimeException {
    private static final long serialVersionUID = -3997931295353156251L;

    private final ErrorCode errorCode;

    public DailyActivitiesException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
