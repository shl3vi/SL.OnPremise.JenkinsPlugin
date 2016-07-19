package io.sealights.plugins.sealightsjenkins.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahar on 6/6/2016.
 */
public class ReflectionUtils {

    public static List<Method> getGettersMethods(Object object) {
        List<Method> getters = new ArrayList<>();
        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            String methodName = method.getName();
            if ((methodName.startsWith("get") || methodName.startsWith("is"))
                    && !methodName.equals("getClass")) {
                getters.add(method);
            }

        }

        return getters;
    }
}
