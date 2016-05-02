package io.sealigths.plugins.sealightsjenkins.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileAndFolderUtils {

//    public static boolean isFolderExists(String folder){
//        File f = new File(folder);
//        if (!f.exists()) {
//            return false;
//        }
//
//        if (!f.isDirectory()){
//            throw new RuntimeException("'" + folder + "' is not a folder.");
//        }
//
//        return true;
//    }

//    public static boolean createFolder(String folder)
//    {
//        if (isFolderExists(folder))
//            return false;
//
//        File f = new File(folder);
//        return f.mkdir();
//    }
//
//    public static void verifyFolderExists(String folder)
//    {
//        if (!isFolderExists(folder)){
//            createFolder(folder);
//        }
//    }
//
//    public static File getOrCreateFile(String filePath) throws IOException{
//        File file = new File(filePath);
//        if(!file.exists()) {
//            file.createNewFile();
//        }
//
//        return file;
//    }

//    public static void writeAllTextToFile(String text, String fileName)
//    {
//        PrintWriter file = null;
//        try {
//            File fileObj = new File(fileName);
//            if (fileObj.exists())
//            {
//                fileObj.delete();
//            }
//            fileObj.createNewFile();
//
//            file = new PrintWriter(fileObj);
//            file.println(text);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        finally {
//            if (file != null)
//                file.close();
//        }
//
//    }

    public static List<String> findAllFilesWithFilter(String rootpath, Boolean recursive, IncludeExcludeFilter filter){

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

    private static List<String> search(File directory, Boolean recursive, IncludeExcludeFilter filter){

        List<String> returnedFiles = new ArrayList<String>();

        if (!directory.isDirectory()){
            throw new RuntimeException("The specified directory is not a valid directory: " + directory);
        }
        if (!directory.canRead()){
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
