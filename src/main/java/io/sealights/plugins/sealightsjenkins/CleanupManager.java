package io.sealights.plugins.sealightsjenkins;

import io.sealights.plugins.sealightsjenkins.utils.FileUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahar on 5/19/2016.
 */
public class CleanupManager {

    private List<String> files = new ArrayList<String>();
    private Logger logger;

    public CleanupManager(Logger logger) {
        this.logger = logger;
    }

    public void addFile(String file) {
        files.add(file);
    }

    public void clean() throws IOException, InterruptedException {
        for (String file : files) {
            try {
                FileUtils.tryDeleteFile(logger, file);
            } catch (FileSystemException e) {
                logger.warning("Failed to delete file: " + file + ". Reason: " + e.getMessage());
            }
        }
    }
}