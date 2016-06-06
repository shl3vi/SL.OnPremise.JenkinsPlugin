package io.sealigths.plugins.sealightsjenkins.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static String join(String[] strings, char delimiter){

        if (strings == null){
            return null;
        }

        List<String> stringsAsList = Arrays.asList(strings);
        return join(stringsAsList,delimiter);
    }
    public static String join(List<String> strings, char delimiter){

        if (strings == null){
            return null;
        }

        StringBuilder retString = new StringBuilder();

        if (!strings.isEmpty()){
            for(int i=0; i<strings.size()-1; i++){
                retString.append(strings.get(i));
                retString.append(delimiter);
            }
            retString.append(strings.get(strings.size()-1));
        }

        return retString.toString();
    }

    public static boolean isNullOrEmpty(String str){
        return (str == null || "".equals(str));
    }
}
