package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.ConfigCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.*;

/**
 * Executor for the 'config' command.
 */
public class ConfigCommandExecutor extends AbstractCommandExecutor {

    private static String BUILD_SESSION_ID_ENV_VAR = "SL_BUILD_SESSION_ID";
    private static String BUILD_SESSION_ID_FILE_ENV_VAR = "SL_BUILD_SESSION_ID_FILE";
    private static String BUILD_SESSION_ID_FILE_NAME = "buildSessionId.txt";

    private BaseCommandArguments baseArgs = null;
    private String buildSessionIdFile = null;
    private ConfigCommandArguments configCommandArguments;
    private JenkinsUtils jenkinsUtils = new JenkinsUtils();

    public ConfigCommandExecutor(Logger logger, ConfigCommandArguments configCommandArguments) {
        super(logger, configCommandArguments.getBaseArgs());
        this.configCommandArguments = configCommandArguments;
        this.baseArgs = configCommandArguments.getBaseArgs();
    }

    @Override
    public boolean execute() {

        try {
            String workingDir = jenkinsUtils.getWorkspace(baseArgs.getBuild());
            this.buildSessionIdFile = PathUtils.join(workingDir, BUILD_SESSION_ID_FILE_NAME);

            boolean isSuccess = super.execute();
            if (isSuccess) {
                // update environment variables
                injectBuildSessionIdEnvVars(baseArgs.getBuild(), logger);
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

    private void injectBuildSessionIdEnvVars(AbstractBuild<?, ?> build, Logger logger) {
        try {
            ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();
            String buildSessionId = argumentFileResolver.resolve(logger, null/*force get from file*/, buildSessionIdFile);

            EnvVarsInjector envVarsInjector = new EnvVarsInjector();
            envVarsInjector.addEnvVariableToBuild(BUILD_SESSION_ID_ENV_VAR, buildSessionId);
            envVarsInjector.addEnvVariableToBuild(BUILD_SESSION_ID_FILE_ENV_VAR, buildSessionIdFile);
            envVarsInjector.inject(build);
        } catch (Exception e) {
            throw new RuntimeException("Failed during Build Session Id environment variables injection", e);
        }
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "buildsessionidfile", this.buildSessionIdFile);
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
