package io.sealigths.plugins.sealightsjenkins.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadav on 4/26/2016.
 */
public class FileUtils {

    private static List<String> searchFilesByExtension(File directory, Boolean recursive, String extension){

        List<String> returnedFiles = new ArrayList<String>();

        if (directory == null)
            throw new IllegalArgumentException("directory cannot be null");

        if (!directory.isDirectory()){
            throw new RuntimeException("The specified directory is not a valid directory: " + directory);
        }
        if (!directory.canRead()){
            throw new RuntimeException("There is no permission to read from the specified directory: " + directory);
        }
        File[] files = directory.listFiles();

        if (files != null)
        {
            for (File fileOrFolder : files) {
                if (fileOrFolder.isDirectory() && recursive) {
                    returnedFiles.addAll(searchFilesByExtension(fileOrFolder, recursive,extension));
                } else {
                    String name = fileOrFolder.getName();
                    String fileExtension = getFileExtension(name);
                    if (fileExtension.equalsIgnoreCase(extension)){
                        returnedFiles.add(fileOrFolder.getAbsoluteFile().toString());
                    }
                }
            }
        }

        return returnedFiles;
    }

    public static String getFileExtension(String file) {
        String extension = "";
        int lastDot = file.lastIndexOf('.');
        if (lastDot > 0) {
            extension = file.substring(lastDot+1);
        }
        return extension;
    }
}
