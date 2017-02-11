package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.FilePath;
import io.sealights.plugins.sealightsjenkins.CleanupManager;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.UploadReportsCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executor for the 'uploadReports' command.
 */
public class UploadReportsCommandExecutor extends AbstractCommandExecutor {

    private UploadReportsCommandArguments uploadReportsCommandArguments;
    private List<String> reportFiles;
    private List<String> reportFilesFolders;
    private CleanupManager cleanupManager;

    public UploadReportsCommandExecutor(Logger logger, UploadReportsCommandArguments uploadReportsCommandArguments) {
        super(logger, uploadReportsCommandArguments.getBaseArgs());
        this.uploadReportsCommandArguments = uploadReportsCommandArguments;
        this.cleanupManager = new CleanupManager(logger);
    }

    @Override
    public boolean execute() {

        try {
            this.reportFiles = resolveFilesList(uploadReportsCommandArguments.getReportFiles());
            this.reportFilesFolders = resolveFilesList(uploadReportsCommandArguments.getReportsFolders());

            FilePath workspace = baseArgs.getBuild().getWorkspace();
            boolean isSlaveMachine = workspace.isRemote();
            if (isSlaveMachine) {
                // The execution is performed on the master..
                copyFilesToMaster();
            }

            boolean isSuccess = super.execute();

            if (isSuccess) {
                if (isSlaveMachine) {
                    deleteTempFilesOnMaster();
                }
                return true;
            } else {
                logger.error("Failed to create SeaLights Build Session Id");
            }
        } catch (Exception e) {
            logger.error("Failed to create SeaLights Build Session Id due to an error. Error:", e);
        }

        return false;
    }

    private List<String> resolveFilesList(String commaSeparatedToList) {
        List<String> tempList = StringUtils.commaSeparatedToList(commaSeparatedToList);
        List<String> returnedList = new ArrayList<>();

        for (String fileName : tempList) {
            if (StringUtils.isNullOrEmpty(fileName)){
                continue;
            }
            returnedList.add(JenkinsUtils.resolveEnvVarsInString(baseArgs.getEnvVars(), fileName));
        }
        return returnedList;
    }

    /*
    * For when working on slave
    * */
    private void copyFilesToMaster() throws IOException, InterruptedException {
        String tempFolder = System.getProperty("java.io.tmpdir");
        copyReportFilesToMaster(tempFolder);
        copyReportFoldersToMaster(tempFolder);
    }

    private void copyReportFoldersToMaster(String tempFolder) throws IOException, InterruptedException {
        List<String> reportFilesFoldersOnMaster = new ArrayList<>();
        for (String folder : this.reportFilesFolders) {
            String folderNameOnMaster = PathUtils.join(tempFolder, "reportsfolder-" + System.nanoTime());
            reportFilesFoldersOnMaster.add(folderNameOnMaster);

            // copy to master from slave
            boolean isFolder = true;
            CustomFile fpOnSlave =  new CustomFile(logger, cleanupManager, folder, isFolder);
            fpOnSlave.copyToMaster(folderNameOnMaster);
        }
        this.reportFilesFolders = reportFilesFoldersOnMaster;
    }

    private void copyReportFilesToMaster(String tempFolder) throws IOException, InterruptedException {
        List<String> reportFilesOnMaster = new ArrayList<>();
        for (String report : this.reportFiles) {
            String fileNameOnMaster = PathUtils.join(tempFolder, "reportfile-" + System.nanoTime());
            reportFilesOnMaster.add(fileNameOnMaster);

            // copy to master from slave
            CustomFile fpOnSlave =  new CustomFile(logger, cleanupManager, report);
            fpOnSlave.copyToMaster(fileNameOnMaster);
        }
        this.reportFiles = reportFilesOnMaster;
    }

    /*
    * For when working on slave
    * */
    private void deleteTempFilesOnMaster() throws IOException, InterruptedException {
        this.cleanupManager.clean();
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();

        for (String reportFile : reportFiles) {
            addArgumentKeyVal(sb, "reportFile", reportFile);
        }


        for (String reportFolder : reportFilesFolders) {
            addArgumentKeyVal(sb, "reportFilesFolder", JenkinsUtils.resolveEnvVarsInString(baseArgs.getEnvVars(), reportFolder));
        }

        addArgumentKeyVal(sb, "hasMoreRequests", String.valueOf(uploadReportsCommandArguments.isHasMoreRequests()));
        addArgumentKeyVal(sb, "source", uploadReportsCommandArguments.getSource());

        return sb.toString();
    }
}
