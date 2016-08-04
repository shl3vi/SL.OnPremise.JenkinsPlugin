package io.sealights.plugins.sealightsjenkins.utils;

import io.sealights.plugins.sealightsjenkins.CleanupManager;

import java.io.IOException;

/**
 * Created by shahar on 5/25/2016.
 */
public class CustomFile {

    Logger logger;
    String name;
    CleanupManager cleanupManager;

    public CustomFile(Logger logger, CleanupManager cleanupManager, String name) {
        this.logger = logger;
        this.cleanupManager = cleanupManager;
        this.name = name;
    }

    public void copyToSlave() throws IOException, InterruptedException {
        copyToSlave(name, false, true);
    }

    public void copyToSlave(boolean deleteSourceFile, boolean deleteTargetFile) throws IOException, InterruptedException {
        copyToSlave(name, deleteSourceFile, deleteTargetFile);
    }

    public void copyToSlave(String targetFile) throws IOException, InterruptedException {
        copyToSlave(targetFile, false, true);
    }

    public void copyToSlave(String targetFile, boolean deleteSourceFile, boolean deleteTargetFile) throws IOException, InterruptedException {
        FileUtils.tryCopyFileFromLocalToSlave(logger, name, targetFile);
        if (deleteSourceFile)
            cleanupManager.addFile(name);
        if (deleteTargetFile)
            cleanupManager.addFile(targetFile);
    }
}
