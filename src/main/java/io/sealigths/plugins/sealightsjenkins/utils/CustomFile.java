package io.sealigths.plugins.sealightsjenkins.utils;

import io.sealigths.plugins.sealightsjenkins.CleanupManager;

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
        copyToSlave(name, true);
    }

    public void copyToSlave(String targetFile) throws IOException, InterruptedException {
        copyToSlave(targetFile, true);
    }

    public void copyToSlave(String targetFile, boolean deleteOnExit) throws IOException, InterruptedException {
        FileUtils.tryCopyFileFromLocalToSlave(logger, name, targetFile);
        if (deleteOnExit)
            cleanupManager.addFile(name);
    }
}
