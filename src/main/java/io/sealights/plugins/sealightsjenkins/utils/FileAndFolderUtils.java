package io.sealights.plugins.sealightsjenkins.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileAndFolderUtils {

    public static boolean verifyFolderExists(String folder) {
        if (folder == null) {
            throw new NullPointerException("Argument 'folder' can't be 'null'.");
        }
        File f = new File(folder);
        if (f.isFile()) {
            throw new IllegalArgumentException("'" + folder + "' should be path to a folder and not to a file.");
        }

        return f.isDirectory() || f.mkdirs();
    }


    public static File findFileInFolder(String folder, String fileName) {
        File folderFile = new File(folder);
        File[] files = folderFile.listFiles();

        if (files == null)
            return null;

        for (File f : files) {
            if (f.getName().equals(fileName))
                return f;
        }

        return null;
    }

}
