package io.sealights.plugins.sealightsjenkins.utils;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadav on 5/15/2016.
 */

public class SearchFileCallable implements FilePath.FileCallable<List<String>> {
    private static final long serialVersionUID = 1L;
    //private Logger logger;
    //private IncludeExcludeFilter filter;
    private String includes;

    public SearchFileCallable(String includes) {
        this.includes = includes;
    }


    @Override
    public List<String> invoke(File rootDirAsFile, VirtualChannel channel)
            throws IOException, InterruptedException {

        List<String> result = new ArrayList<>();
        FilePath rootDir = new FilePath(rootDirAsFile);
        if (rootDir.isDirectory()) {
            return search(rootDir, true);
        } else {
            return result;
        }

    }

    private List<String> search(FilePath directory, Boolean recursive) throws IOException, InterruptedException {

        List<String> returnedFiles = new ArrayList<String>();

        if (!directory.isDirectory()) {
            throw new RuntimeException("The specified directory is not a valid directory: " + directory);
        }

        FilePath[] files = directory.list(includes);
        if (files != null) {
            for (FilePath fileOrFolder : files) {
                if (fileOrFolder == null) {
                    continue;
                }
                if (fileOrFolder.isDirectory() && recursive) {
                    returnedFiles.addAll(search(fileOrFolder, recursive));
                } else {
                    returnedFiles.add(fileOrFolder.absolutize().toString());
                }
            }
        }
        return returnedFiles;
    }

    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }
}