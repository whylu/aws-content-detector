package ming.test.model;

public enum ErrorCode {
    MISSING_CONTENT(-1),
    DB_CONNECT_FAILED(-2),
    DB_INSERT_FAILED(-3),

    ;

    private final int code;
    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
