package io.sealigths.plugins.sealightsjenkins;

import io.sealigths.plugins.sealightsjenkins.utils.FileUtils;
import io.sealigths.plugins.sealightsjenkins.utils.Logger;

import java.io.IOException;
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
            FileUtils.tryDeleteFile(logger, file);
        }
    }
}
