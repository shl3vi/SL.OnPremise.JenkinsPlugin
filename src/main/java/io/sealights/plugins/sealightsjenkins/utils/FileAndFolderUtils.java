package io.sealights.plugins.sealightsjenkins.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileAndFolderUtils {

    public static String readFileFromResources(String fileName, Logger logger) throws FileNotFoundException {
        //Get file from resources folder
        InputStream inputStream = FileAndFolderUtils.class.getClassLoader().getResourceAsStream(fileName);
        String content = StreamUtils.toString(inputStream);
        return content;
    }

    public static List<String> findAllFilesWithFilter(String rootpath, Boolean recursive, IncludeExcludeFilter filter) {

        List<String> result = new ArrayList<>();
        File rootDir = new File(rootpath);
        if (rootDir.isFile()) {
            if (filter.filter(rootpath)) {
                result.add(rootpath);
            }
            return result;
        } else if (rootDir.isDirectory()) {
            return search(rootDir, recursive, filter);
        }

        return result;
    }

    private static List<String> search(File directory, Boolean recursive, IncludeExcludeFilter filter) {

        List<String> returnedFiles = new ArrayList<String>();

        if (!directory.isDirectory()) {
            throw new RuntimeException("The specified directory is not a valid directory: " + directory);
        }
        if (!directory.canRead()) {
            throw new RuntimeException("There is no permission to read from the specified directory: " + directory);
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File fileOrFolder : files) {
                if (fileOrFolder == null)
                    continue;
                if (fileOrFolder.isDirectory() && recursive) {
                    returnedFiles.addAll(search(fileOrFolder, recursive, filter));
                } else {
                    if (filter.filter(fileOrFolder.getName())) {
                        returnedFiles.add(fileOrFolder.getAbsoluteFile().toString());
                    }
                }
            }
        }
        return returnedFiles;
    }

//    public static String getFileExtension(String file) {
//        String extension = "";
//        int lastDot = file.lastIndexOf('.');
//        if (lastDot > 0) {
//            extension = file.substring(lastDot+1);
//        }
//        return extension;
//    }


}
