package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.CleanupManager;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.ConfigCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.*;

import java.io.IOException;

/**
 * Executor for the 'config' command.
 */
public class ConfigCommandExecutor extends AbstractCommandExecutor {

    private static String BUILD_SESSION_ID_ENV_VAR = "SL_BUILD_SESSION_ID";
    private static String BUILD_SESSION_ID_FILE_ENV_VAR = "SL_BUILD_SESSION_ID_FILE";
    private static String BUILD_SESSION_ID_FILE_NAME = "buildSessionId.txt";

    private String buildSessionIdFileOnMaster = null;
    private ConfigCommandArguments configCommandArguments;
    private JenkinsUtils jenkinsUtils = new JenkinsUtils();
    private CleanupManager cleanupManager;

    public ConfigCommandExecutor(
            Logger logger, BaseCommandArguments baseCommandArguments, ConfigCommandArguments configCommandArguments) {
        super(logger, baseCommandArguments);
        this.configCommandArguments = configCommandArguments;
        this.cleanupManager = new CleanupManager(logger);
    }

    @Override
    public boolean execute() {

        try {
            FilePath workspace = baseArgs.getBuild().getWorkspace();

            // Resolving on master, even when workspace is remote, since this jar is running on master
            // After the execution, we will copy the file to the slave
            resolveBuildSessionIdFileOnMaster(workspace);

            boolean isSuccess = super.execute();

            if (isSuccess) {
                onSuccess(baseArgs.getBuild(), workspace, logger);
                logger.info("Created SeaLights Build Session Id successfully");
                return true;
            } else {
                logger.error("Failed to create SeaLights Build Session Id");
            }
        } catch (Exception e) {
            logger.error("Failed to create SeaLights Build Session Id due to an error. Error:", e);
        }

        return false;
    }

    private void resolveBuildSessionIdFileOnMaster(FilePath workspace){
        boolean isSlaveMachine = workspace.isRemote();

        if (isSlaveMachine) {
            this.buildSessionIdFileOnMaster = createTempPathToFileOnMaster();
        } else {
            String workingDir = jenkinsUtils.getWorkspace(baseArgs.getBuild());
            this.buildSessionIdFileOnMaster = PathUtils.join(workingDir, BUILD_SESSION_ID_FILE_NAME);
        }
    }

    private String createTempPathToFileOnMaster(){
        String tempFolder = System.getProperty("java.io.tmpdir");
        String fileName = "buildSession_" + System.currentTimeMillis() + ".txt";
        return PathUtils.join(tempFolder, fileName);
    }

    private String copyBuildSessionFileToSlave(FilePath workspace) {
        try {
            CustomFile fileOnMaster = new CustomFile(logger, cleanupManager, this.buildSessionIdFileOnMaster);
            String fileOnSlave = PathUtils.join(workspace.getRemote(), BUILD_SESSION_ID_FILE_NAME);
            fileOnMaster.copyToSlave(fileOnSlave);
            return fileOnSlave;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy the build session id file to the remote node.", e);
        }
    }

    private void onSuccess(
            AbstractBuild<?, ?> build, FilePath workspace, Logger logger) throws IOException, InterruptedException {

        // get the buildSessionId from the created file
        ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();
        String buildSessionId = argumentFileResolver.resolve(logger, null/*force get from file*/, buildSessionIdFileOnMaster);

        if (workspace.isRemote()) {
            // copy the created temp file from master to the slave
            String fileOnSlave = copyBuildSessionFileToSlave(workspace);

            // delete the created temp file
            cleanupManager.clean();

            injectBuildSessionIdEnvVars(build, buildSessionId, fileOnSlave, logger);

        }else {
            injectBuildSessionIdEnvVars(build, buildSessionId, this.buildSessionIdFileOnMaster, logger);
        }
    }

    private void injectBuildSessionIdEnvVars(
            AbstractBuild<?, ?> build, String buildSessionId, String createdFile, Logger logger) {
        try {
            EnvVarsInjector envVarsInjector = new EnvVarsInjector(build, logger);
            envVarsInjector.addEnvVariableToBuild(BUILD_SESSION_ID_ENV_VAR, buildSessionId);
            envVarsInjector.addEnvVariableToBuild(BUILD_SESSION_ID_FILE_ENV_VAR, createdFile);
            envVarsInjector.inject();
        } catch (Exception e) {
            throw new RuntimeException("Failed during Build Session Id environment variables injection", e);
        }
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "buildsessionidfile", this.buildSessionIdFileOnMaster);
        addArgumentKeyVal(sb, "packagesincluded", configCommandArguments.getPackagesIncluded());
        addArgumentKeyVal(sb, "packagesexcluded", configCommandArguments.getPackagesExcluded());
        sb.append("-enableNoneZeroErrorCode");

        return sb.toString();
    }

    @Override
    protected String getCommandName() {
        return "-config";
    }

    public void setJenkinsUtils(JenkinsUtils jenkinsUtils) {
        this.jenkinsUtils = jenkinsUtils;
    }
}
