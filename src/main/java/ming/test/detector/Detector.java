package ming.test.detector;


import ming.test.utils.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface Detector {
    boolean foundSuspicion(String content);


    class Factory {

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
                LogUtils.log("Detector class not found: "+ className);
                return null;
            } catch ( InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException ex) {
                LogUtils.log("newInstance failed, ex"+ ex.getMessage());
                return null;
            }
        }
    }
}
