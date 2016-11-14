package io.sealights.plugins.sealightsjenkins.utils;

import java.util.Properties;

/**
 * Created by shahar on 11/8/2016.
 */
public class PropertiesUtils {

    private static final String newLine = System.getProperty("line.separator");

    public static Properties toProperties(String propsString){

        Properties p = new Properties();
        if (StringUtils.isNullOrEmpty(propsString)) {
            return p;
        }

        String propsStringFixed = StringUtils.fixOSEnding(propsString);
        String[] propsArray = propsStringFixed.split(newLine);
        for (String keyValPair : propsArray){
            String[] keyValArray = keyValPair.split("=");
            if (keyValArray.length != 2){
                continue;
            }

            String key = keyValArray[0].trim();
            String value = keyValArray[1].trim();

            if (isValidKey(key)){
                p.put(key, value);
            }
        }

        return p;
    }

    private static boolean isValidKey(String key) {
        return !key.contains(" ");
    }
}
