package io.sealights.plugins.sealightsjenkins.buildsteps.cli;

import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import io.sealights.plugins.sealightsjenkins.BeginAnalysis;
import io.sealights.plugins.sealightsjenkins.CleanupManager;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.ExternalReportArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.SealightsBuildStatus;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.utils.BuildNameResolver;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.entities.ValidationError;
import io.sealights.plugins.sealightsjenkins.utils.*;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class creates build status report file on the file system and then
 * sends it to the server using the 'externalReport' CLI command.
 */
public class BuildStatusNotifier extends Notifier {

    private boolean enabled;
    private String buildSessionId;
    private String appName;
    private String branchName;
    private CommandBuildName buildName;
    private String additionalArguments;
    private BeginAnalysis beginAnalysis = new BeginAnalysis();

    /*
    * Start - For when working on slave
    * */
    private boolean isSlaveMachine = false;
    private String reportPathOnMaster = null;
    /*
    * End - For when working on slave
    * */


    @DataBoundConstructor
    public BuildStatusNotifier(boolean enabled, String buildSessionId, String appName, String branchName,
                               CommandBuildName buildName, String additionalArguments) {
        super();
        this.enabled = enabled;
        this.buildSessionId = buildSessionId;
        this.appName = appName;
        this.branchName = branchName;
        this.buildName = buildName;
        this.additionalArguments = additionalArguments;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        Logger logger = new Logger(listener.getLogger(), "SeaLights Build Status Notifier");
        try {
            // This step must be first
            setDefaultValues();
            if (!enabled) {
                logger.info("'Report Build Status' is disabled.");
                return true;
            }

            this.isSlaveMachine = build.getWorkspace().isRemote();

            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            EnvVars envVars = build.getEnvironment(listener);

            String filesStorage = resolveFilesStorage(additionalProps, envVars);

            BaseCommandArguments baseCommandArguments = createBaseCommandArguments(
                    build, envVars, additionalProps, logger);

            String workingDir = findWorkingDirPath(build);

            // we creates a report file on the file system
            String reportFilePath = resolveReportFilePath(workingDir, logger);
            createReportFile(build.getResult(), reportFilePath, logger);

            logger.info("About to report build status.");

            // we sends the created report file to the server using SeaLights cli
            ExternalReportArguments externalReportArguments = new ExternalReportArguments(reportFilePath);
            CLIHandler cliHandler =
                    new CLIHandler(baseCommandArguments, externalReportArguments, filesStorage, logger);

            boolean isSuccess = cliHandler.handle();

            if (isSuccess) {
                onSuccess(envVars, additionalProps, reportFilePath, workingDir, logger);
            }

        } catch (Exception e) {
            logger.error("Failed to send build status report. Error: ", e);
        }
        return true;
    }

    private void onSuccess(
            EnvVars envVars, Properties additionalProps, String createdReport, String workingDir, Logger logger)
            throws IOException, InterruptedException {

        String keepReportString = resolveEnvVar(envVars, (String) additionalProps.get("keepreport"));
        Boolean keepReport = Boolean.valueOf(keepReportString);

        if (keepReport) {
            keepReportFile(createdReport, workingDir, logger);
        } else {
            dontKeepReportFile(createdReport, logger);
        }
    }

    private void keepReportFile(String createdReport, String workingDir, Logger logger)
            throws IOException, InterruptedException {

        if (!isSlaveMachine) {
            return; // the report is already on the master machine so do nothing
        }

        CleanupManager cleanupManager = new CleanupManager(logger);
        CustomFile reportOnMaster = new CustomFile(logger, cleanupManager, createdReport);

        // we are copying the report file from temp location at the master to the slave working directory
        String reportPathOnSlave = PathUtils.join(workingDir, "buildStatusReport_" + UUID.randomUUID() + ".json");
        boolean deleteFileOnSlave = true, deleteFileOnMaster = true;
        reportOnMaster.copyToSlave(reportPathOnSlave, deleteFileOnMaster, !deleteFileOnSlave);

        cleanupManager.clean();

    }

    private void dontKeepReportFile(String createdReport, Logger logger) throws IOException, InterruptedException {
        if (isSlaveMachine) {
            return; // the report wasn't created on the slave machine
        }

        FileUtils.tryDeleteFile(logger, createdReport);
    }

    private String createTempPathToFileOnMaster() {
        String tempFolder = System.getProperty("java.io.tmpdir");
        String fileName = "reportStatus_" + UUID.randomUUID() + ".txt";
        return PathUtils.join(tempFolder, fileName);
    }

    private void setDefaultValues() {
        if (this.buildName == null)
            this.buildName = new CommandBuildName.EmptyBuildName();
    }

    private String resolveReportFilePath(String workingDir, Logger logger) {
        String fileName;

        if (isSlaveMachine) {
            // create a copy of the file at the master machine because the file will be reported from the master machine.
            this.reportPathOnMaster = createTempPathToFileOnMaster();
            fileName = this.reportPathOnMaster;
        } else {
            // we are trying to create the report at the working directory
            fileName = PathUtils.join(workingDir, "buildStatusReport_" + UUID.randomUUID() + ".json");
        }

        logger.info("Report file location: '" + fileName + "'");
        return fileName;
    }

    private String findWorkingDirPath(AbstractBuild<?, ?> build) {
        String workingDir;
        FilePath ws = build.getWorkspace();
        if (ws == null || (workingDir = ws.getRemote()) == null) {
            // if we can't resolve the working directory, we will create the report at the temp directory
            workingDir = System.getProperty("java.io.tmpdir");
        }

        return workingDir;
    }

    private void createReportFile(final Result result, String fileName, Logger logger) {
        Map reportMap = createReportMap(result);
        saveReportToFS(reportMap, fileName, logger);
    }

    private Map createReportMap(Result result) {
        String status = toSealightsBuildStatus(result);
        String title = "CI Status";
        String fieldName = "status";
        String type = "string";
        String value = result.toString();

        List<Map> data = new ArrayList<>();
        Map index0 = new HashMap();
        index0.put("fieldName", fieldName);
        index0.put("type", type);
        index0.put("value", value);
        data.add(index0);

        Map<String, Object> externalReport = new HashMap<>();
        externalReport.put("title", title);
        externalReport.put("data", data);
        externalReport.put("status", status);

        return externalReport;
    }

    private String toSealightsBuildStatus(Result result) {

        // possible statuses SUCCESS, UNSTABLE, FAILURE, NOT_BUILT, ABORTED
        if (Result.SUCCESS == result) {
            return SealightsBuildStatus.SUCCESS.getName();
        } else if (Result.UNSTABLE == result || Result.FAILURE == result || Result.ABORTED == result) {
            return SealightsBuildStatus.FAILURE.getName();
        } else {
            // return empty status for every other result ('null' || NOT_BUILT)
            return null;
        }
    }

    private void saveReportToFS(Map reportMap, String reportFilePath, Logger logger) {
        try {
            logger.info("Try to create report file at '" + reportFilePath + "'");
            File reportFile = new File(reportFilePath);
            if (!reportFile.createNewFile()) {
                throw new RuntimeException("Failed to create new file at '" + reportFilePath + "' for the report.");
            }
            JsonSerializer.serializeToFile(reportFile, reportMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create report file at '" + reportFilePath + "'", e);
        }
    }

    private BaseCommandArguments createBaseCommandArguments(
            AbstractBuild<?, ?> build, EnvVars envVars, Properties additionalProps, Logger logger) {

        BaseCommandArguments baseArgs = new BaseCommandArguments();

        String globalToken = beginAnalysis.getDescriptor().getToken();
        baseArgs.setToken(resolveGlobalArgument(envVars, additionalProps, "token", globalToken));
        baseArgs.setTokenFile(resolveEnvVar(envVars, (String) additionalProps.get("tokenfile")));

        // need to create tokenData for the upgrade feature (need to know to which server it should request for agents)
        TokenData tokenData = createTokenData(baseArgs.getToken(), baseArgs.getTokenFile(), logger);
        baseArgs.setTokenData(tokenData);

        String globalProxy = beginAnalysis.getDescriptor().getProxy();
        baseArgs.setProxy(resolveGlobalArgument(envVars, additionalProps, "proxy", globalProxy));

        baseArgs.setBuildSessionId(resolveEnvVar(envVars, buildSessionId));
        baseArgs.setBuildSessionIdFile(resolveEnvVar(envVars, (String) additionalProps.get("buildsessionidfile")));

        baseArgs.setAppName(resolveEnvVar(envVars, appName));

        BuildNameResolver buildNameResolver = new BuildNameResolver();
        baseArgs.setBuildName(buildNameResolver.getFinalBuildName(build, envVars, buildName, logger));

        baseArgs.setBranchName(resolveEnvVar(envVars, branchName));

        baseArgs.setAgentPath(resolveEnvVar(envVars, (String) additionalProps.get("agentpath")));
        baseArgs.setJavaPath(resolveEnvVar(envVars, (String) additionalProps.get("javapath")));

        baseArgs.setBuild(build);
        baseArgs.setEnvVars(envVars);
        baseArgs.setLogger(logger);

        return baseArgs;
    }

    private String resolveGlobalArgument(EnvVars envVars, Properties additionalProps, String name, String globalValue) {
        String global = JenkinsUtils.resolveEnvVarsInString(envVars, (String) additionalProps.get(name));
        if (StringUtils.isNullOrEmpty(global)) {
            global = globalValue;
        }
        return global;
    }

    private String resolveFilesStorage(Properties additionalProps, EnvVars envVars) {
        String filesStorage = (String) additionalProps.get("filesstorage");
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return resolveEnvVar(envVars, filesStorage);
        }

        filesStorage = this.beginAnalysis.getDescriptor().getFilesStorage();
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return filesStorage;
        }

        return System.getProperty("java.io.tmpdir");
    }

    private String resolveEnvVar(EnvVars envVars, String envVarKey) {
        return JenkinsUtils.resolveEnvVarsInString(envVars, envVarKey);
    }

    private TokenData createTokenData(String token, String tokenFile, Logger logger) {
        TokenData tokenData;

        ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();
        token = argumentFileResolver.resolve(logger, token, tokenFile);

        try {
            tokenData = TokenData.parse(token);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid token. Error: ", e);
        }

        TokenValidator tokenValidator = new TokenValidator();
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);
        if (validationErrors.size() > 0) {
            logger.error("Invalid token. The token contains the following errors:");
            for (ValidationError validationError : validationErrors) {
                logger.error("Field: '" + validationError.getName() + "', Error: '" + validationError.getProblem() + "'.");
            }
            throw new RuntimeException("Invalid token.");
        }

        return tokenData;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        //This is here to ensure that the reported build status is actually correct. If we were to return false here,
        //other build plugins could still modify the build result, making the sent out HipChat notification incorrect.
        return true;
    }

    @Exported
    public boolean isEnabled() {
        return enabled;
    }

    @Exported
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Exported
    public String getBuildSessionId() {
        return buildSessionId;
    }

    @Exported
    public void setBuildSessionId(String buildSessionId) {
        this.buildSessionId = buildSessionId;
    }

    @Exported
    public String getAppName() {
        return appName;
    }

    @Exported
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Exported
    public String getBranchName() {
        return branchName;
    }

    @Exported
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Exported
    public CommandBuildName getBuildName() {
        return buildName;
    }

    @Exported
    public void setBuildName(CommandBuildName buildName) {
        this.buildName = buildName;
    }

    @Exported
    public String getAdditionalArguments() {
        return additionalArguments;
    }

    @Exported
    public void setAdditionalArguments(String additionalArguments) {
        this.additionalArguments = additionalArguments;
    }

    @Exported
    public BeginAnalysis getBeginAnalysis() {
        return beginAnalysis;
    }

    @Exported
    public void setBeginAnalysis(BeginAnalysis beginAnalysis) {
        this.beginAnalysis = beginAnalysis;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "SeaLights - Report Build Status";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public DescriptorExtensionList<CommandBuildName, CommandBuildName.CommandBuildNameDescriptor> getBuildNameDescriptorList() {
            return Jenkins.getInstance().getDescriptorList(CommandBuildName.class);
        }

    }
}