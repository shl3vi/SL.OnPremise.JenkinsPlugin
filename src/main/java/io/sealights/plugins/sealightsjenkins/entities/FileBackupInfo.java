package io.sealights.plugins.sealightsjenkins.entities;

/**
 * Created by Nadav on 5/4/2016.
 */
public class FileBackupInfo {
    private String sourceFile;
    private String targetFile;

    public FileBackupInfo(String source, String target)
    {
        this.sourceFile = source;
        this.targetFile = target;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }
}
