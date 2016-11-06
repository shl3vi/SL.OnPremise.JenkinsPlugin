package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Created by shahar on 11/4/2016.
 */
public class UploadReportsCommandArguments extends AbstractCommandArguments{

    private String reportFiles;
    private String reportsFolders;
    private boolean hasMoreRequests;
    private String source;

    public UploadReportsCommandArguments( CommonCommandArguments commonCommandArguments, String reportFiles, String reportsFolders, boolean hasMoreRequests, String source) {
        super(CommandModes.UploadReports, commonCommandArguments);
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
}
