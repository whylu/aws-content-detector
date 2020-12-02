package ming.test.config;

public class Environment {
    public String getDbUrl() {
        return System.getenv("DB_URL");
    }

    public String getDbUsername() {
        return System.getenv("DB_USERNAME");
    }

    public String getDbPassword() {
        return System.getenv("DB_PASSWORD");
    }

    public String getDetectStrategy() {
        return System.getenv("DETECT_STRATEGY");
    }

}
