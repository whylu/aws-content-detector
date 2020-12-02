package ming.test.model;

public enum ErrorCode {
    MISSING_CONTENT(-1, "-1"),
    DB_CONNECT_FAILED(-2, "-2"),
    DB_INSERT_FAILED(-3, "-3"),

    ROW_ID_INVALID(-4, "-4"),
    ORDER_NOT_FOUND(-5, "-5"),
    DETECTOR_NOT_DEFINED(-6, "-6")
    ;

    private final int code;
    private final String codeStr;
    ErrorCode(int code, String codeStr) {
        this.code = code;
        this.codeStr = codeStr;
    }

    public int getCode() {
        return code;
    }

    public String getCodeStr() {
        return codeStr;
    }
}
