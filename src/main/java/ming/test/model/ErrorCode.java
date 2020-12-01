package ming.test.model;

public enum ErrorCode {
    MISSING_CONTENT(-1, "-1"),
    DB_CONNECT_FAILED(-2, "-2"),
    DB_INSERT_FAILED(-3, "-3"),

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
