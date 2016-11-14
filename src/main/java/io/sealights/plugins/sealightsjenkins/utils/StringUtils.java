package io.sealights.plugins.sealightsjenkins.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static final String windows_newline = "\r\n";
    public static final String unix_newline = "\n";

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

    public static List<String> commaSeparatedToList(String str){
        if (str == null){
            return new ArrayList<>();
        }
        return  Arrays.asList(str.split("\\s*,\\s*"));
    }

    public static String fixOSEnding(String str){
        if (OsDetector.isWindows())
            return str.replace(unix_newline, windows_newline);

        return str.replace(windows_newline, unix_newline);
    }
}
