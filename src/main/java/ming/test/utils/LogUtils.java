package ming.test.utils;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LogUtils {
    static LambdaLogger logger;
    public static void set(LambdaLogger theLogger) {
        logger = theLogger;
    }
    public static void log(String message) {
        if(logger!=null) {
            logger.log(message);
        }
    }
}
