package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Arguments for the 'uploadReports' command.
 */
public class UploadReportsCommandArguments extends AbstractCommandArgument {

    private String reportFiles;
    private String reportsFolders;
    private boolean hasMoreRequests;
    private String source;

    public UploadReportsCommandArguments(String reportFiles, String reportsFolders, boolean hasMoreRequests, String source) {
        this.reportFiles = reportFiles;
        this.reportsFolders = reportsFolders;
        this.hasMoreRequests = hasMoreRequests;
        this.source = source;
    }

    public String getReportFiles() {
        return reportFiles;
    }

    public String getReportsFolders() {
        return reportsFolders;
    }

    public boolean isHasMoreRequests() {
        return hasMoreRequests;
    }

    public String getSource() {
        return source;
    }

    public void setReportFiles(String reportFiles) {
        this.reportFiles = reportFiles;
    }

    public void setReportsFolders(String reportsFolders) {
        this.reportsFolders = reportsFolders;
    }

    @Override
    public CommandModes getMode() {
        return CommandModes.UploadReports;
    }
}
