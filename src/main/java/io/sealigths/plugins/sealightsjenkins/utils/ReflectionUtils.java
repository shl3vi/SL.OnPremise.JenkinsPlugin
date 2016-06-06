package io.sealigths.plugins.sealightsjenkins.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shahar on 6/6/2016.
 */
public class ReflectionUtils {

    public static void printGetters(Object object, Logger logger) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Object value;
            if (!(methodName.startsWith("get") || methodName.startsWith("is"))
                    || methodName.equals("getClass"))
                continue;
            try {
                value = method.invoke(object);
                logger.debug(methodName + " : " + value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error while trying to print method: " + methodName, e);
            }
        }
    }
}
