package io.sealigths.plugins.sealightsjenkins.utils;

import java.io.File;

public class PathUtils {

    public static String join(String... pathItems) {
        StringBuilder sb = new StringBuilder();

        for (String item : pathItems) {
            sb.append(item);
            sb.append(File.separator);
        }

        String result= sb.toString();

        int indexOfLastSeperator = result.lastIndexOf(File.separator);
        if (indexOfLastSeperator == result.length()-1)
        {
            //Remove last slash
            result = result.substring(0, indexOfLastSeperator);
        }
        return result;
    }

    public static String toSystemSeparator(String path){
        return toSystemSeparator(path, "\\");
    }
    public static String toSystemSeparator(String path, String currentSeperator)
    {
        if (path == null || path.length() == 0)
            return path;

        if (path.indexOf(File.separator) < 0)
        {
            path = path.replace(currentSeperator, File.separator);
        }

        return path;
    }
}
