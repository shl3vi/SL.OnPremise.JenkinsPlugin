package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Arguments for the 'uploadReports' command.
 */
public class UploadReportsCommandArguments extends BaseCommandArguments {

    private BaseCommandArguments baseArgs;
    private String reportFiles;
    private String reportsFolders;
    private boolean hasMoreRequests;
    private String source;

    public UploadReportsCommandArguments(BaseCommandArguments baseArgs, String reportFiles, String reportsFolders, boolean hasMoreRequests, String source) {
        this.baseArgs = baseArgs;
        this.reportFiles = reportFiles;
        this.reportsFolders = reportsFolders;
        this.hasMoreRequests = hasMoreRequests;
        this.source = source;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
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
}
