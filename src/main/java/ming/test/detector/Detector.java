package ming.test.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface Detector {
    boolean foundSuspicion(String content);


    class Factory {
        private static Logger logger = LoggerFactory.getLogger(Factory.class);

        public static Detector build(String className) {
            if(className==null) {
                return null;
            }
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getConstructor();
                Object o = constructor.newInstance();
                return (Detector)o;
            } catch (ClassNotFoundException ex) {
                logger.warn("Detector class not found: {}", className);
                return null;
            } catch ( InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException ex) {
                logger.warn("newInstance failed", ex);
                return null;
            }
        }
    }
}
