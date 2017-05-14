package io.sealights.plugins.sealightsjenkins;

import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import hudson.util.FormValidation;
import hudson.util.XStream2;
import io.sealights.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.entities.ValidationError;
import io.sealights.plugins.sealightsjenkins.exceptions.SeaLightsIllegalStateException;
import io.sealights.plugins.sealightsjenkins.integration.MavenIntegration;
import io.sealights.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.MavenPluginUpgradeManager;
import io.sealights.plugins.sealightsjenkins.utils.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by shahar on 5/9/2016.
 */
@ExportedBean
public class BeginAnalysis extends Builder {

    private String buildSessionId;
    private String appName;
    private String moduleName;
    private String branch;
    private boolean enableMultipleBuildFiles;
    private boolean overrideJars;
    private boolean multipleBuildFiles;
    private transient String environment;
    private String testStage;
    private String labId;
    private String packagesIncluded;
    private String packagesExcluded;
    private String filesIncluded;
    private String filesExcluded;
    private String classLoadersExcluded;
    private boolean recursive;
    private String pomPath;
    private String workspacepath;
    private String buildScannerJar;
    private String testListenerJar;
    private transient String apiJar;
    private String testListenerConfigFile;
    private boolean autoRestoreBuildFile;
    private String sealightsMavenPluginInstallationArguments;
    private String buildFilesPatterns;
    private String buildFilesFolders;
    private boolean logEnabled;
    private String logFolder;
    private LogDestination logDestination = LogDestination.CONSOLE;
    private transient TestingFramework testingFramework;
    private LogLevel logLevel = LogLevel.OFF;
    private BuildStrategy buildStrategy = BuildStrategy.ONE_BUILD;
    private BuildName buildName;
    private ExecutionType executionType = ExecutionType.FULL;
    private String slMvnPluginVersion;
    private String override_customerId;
    private String override_url;
    private String override_proxy;
    private String additionalArguments;

    public BeginAnalysis() {
    }

    @DataBoundConstructor
    public BeginAnalysis(LogLevel logLevel, String buildSessionId,
                         String appName, String moduleName, String branch, boolean enableMultipleBuildFiles,
                         boolean overrideJars, boolean multipleBuildFiles, String labId, String testStage,
                         String packagesIncluded, String packagesExcluded, String filesIncluded,
                         String filesExcluded, String classLoadersExcluded, boolean recursive,
                         String workspacepath, String buildScannerJar, String testListenerJar,
                         String testListenerConfigFile, boolean autoRestoreBuildFile,
                         String sealightsMavenPluginInstallationArguments,
                         String buildFilesPatterns, String buildFilesFolders,
                         boolean logEnabled, LogDestination logDestination, String logFolder,
                         BuildStrategy buildStrategy, String slMvnPluginVersion,
                         BuildName buildName, ExecutionType executionType, String override_customerId,
                         String override_url, String override_proxy, String additionalArguments)
            throws IOException {

        this.buildSessionId = buildSessionId;
        this.override_customerId = override_customerId;
        this.override_url = override_url;
        this.override_proxy = override_proxy;
        this.appName = appName;
        this.moduleName = moduleName;
        this.branch = branch;
        this.packagesIncluded = packagesIncluded;
        this.packagesExcluded = packagesExcluded;
        this.filesIncluded = filesIncluded;
        this.filesExcluded = filesExcluded;
        this.classLoadersExcluded = classLoadersExcluded;
        this.recursive = recursive;
        this.workspacepath = workspacepath;
        this.testListenerConfigFile = testListenerConfigFile;
        this.buildStrategy = buildStrategy;
        this.buildName = buildName;
        this.autoRestoreBuildFile = autoRestoreBuildFile;
        this.sealightsMavenPluginInstallationArguments = sealightsMavenPluginInstallationArguments;
        this.testStage = testStage;
        this.labId = labId;
        this.executionType = executionType;
        this.multipleBuildFiles = multipleBuildFiles;
        this.overrideJars = overrideJars;
        this.buildFilesFolders = buildFilesFolders;
        this.buildFilesPatterns = buildFilesPatterns;
        this.logEnabled = logEnabled;
        this.logLevel = logLevel;
        this.logDestination = logDestination;
        this.logFolder = logFolder;

        this.enableMultipleBuildFiles = enableMultipleBuildFiles;

        this.buildScannerJar = buildScannerJar;
        this.testListenerJar = testListenerJar;
        this.slMvnPluginVersion = slMvnPluginVersion;

        this.additionalArguments = additionalArguments;
    }

    private void setDefaultValuesForStrings(Logger logger) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!String.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                if (field.get(this) == null)
                    field.set(this, "");
            } catch (Exception e) {
                logger.error("Failed to set default value for field " + field.getName(), e);
            }
        }
    }

    private void setDefaultValues(Logger logger) {

        if (this.logDestination == null)
            this.logDestination = LogDestination.CONSOLE;

        if (this.logLevel == null)
            this.logLevel = LogLevel.OFF;

        if (this.buildStrategy == null)
            this.buildStrategy = BuildStrategy.ONE_BUILD;

        if (this.buildName == null)
            this.buildName = new BuildName.DefaultBuildName();

        if (this.executionType == null)
            this.executionType = ExecutionType.FULL;

        setDefaultValuesForStrings(logger);
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
    public String getPomPath() {
        return pomPath;
    }

    @Exported
    public ExecutionType getExecutionType() {
        return executionType;
    }

    @Exported
    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    @Exported
    public BuildName getBuildName() {
        return buildName;
    }

    @Exported
    public void setBuildName(BuildName buildName) {
        this.buildName = buildName;
    }

    @Exported
    public String getAppName() {
        return appName;
    }

    @Exported
    public String getModuleName() {
        return moduleName;
    }

    @Exported
    public String getBranch() {
        return branch;
    }

    @Exported
    public boolean isEnableMultipleBuildFiles() {
        return enableMultipleBuildFiles;
    }

    @Exported
    public boolean isOverrideJars() {
        return overrideJars;
    }

    @Exported
    public boolean isMultipleBuildFiles() {
        return multipleBuildFiles;
    }

    @Deprecated
    @Exported
    public String getEnvironment() {
        return environment;
    }

    @Exported
    public String getLabId() {
        return labId;
    }

    @Exported
    public String getTestStage() {
        if (!StringUtils.isNullOrEmpty(testStage)) {
            return testStage;
        }

        if (!StringUtils.isNullOrEmpty(environment)) {
            testStage = environment;
        }

        return testStage;
    }

    @Exported
    public String getPackagesIncluded() {
        return packagesIncluded;
    }

    @Exported
    public String getPackagesExcluded() {
        return packagesExcluded;
    }

    @Exported
    public String getFilesIncluded() {
        return filesIncluded;
    }

    @Exported
    public String getFilesExcluded() {
        return filesExcluded;
    }

    @Exported
    public String getClassLoadersExcluded() {
        return classLoadersExcluded;
    }

    @Exported
    public boolean isRecursive() {
        return recursive;
    }

    @Exported
    public String getWorkspacepath() {
        return workspacepath;
    }

    @Exported
    public String getBuildScannerJar() {
        return buildScannerJar;
    }

    @Exported
    public String getTestListenerJar() {
        return testListenerJar;
    }

    @Exported
    public String getTestListenerConfigFile() {
        return testListenerConfigFile;
    }

    @Exported
    public boolean isAutoRestoreBuildFile() {
        return autoRestoreBuildFile;
    }

    @Exported
    public void setAutoRestoreBuildFile(boolean autoRestoreBuildFile) {
        this.autoRestoreBuildFile = autoRestoreBuildFile;
    }

    @Exported
    public String getSealightsMavenPluginInstallationArguments() {
        return sealightsMavenPluginInstallationArguments;
    }

    @Exported
    public void setSealightsMavenPluginInstallationArguments(String sealightsMavenPluginInstallationArguments) {
        this.sealightsMavenPluginInstallationArguments = sealightsMavenPluginInstallationArguments;
    }

    @Exported
    public String getBuildFilesPatterns() {
        return buildFilesPatterns;
    }

    @Exported
    public String getBuildFilesFolders() {
        return buildFilesFolders;
    }

    @Exported
    public boolean isLogEnabled() {
        return logEnabled;
    }

    @Exported
    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    @Exported
    public LogDestination getLogDestination() {
        return logDestination;
    }

    @Exported
    public void setLogDestination(LogDestination logDestination) {
        this.logDestination = logDestination;
    }

    @Exported
    public String getLogFolder() {
        return logFolder;
    }

    @Exported
    public LogLevel getLogLevel() {
        return logLevel;
    }

    @Exported
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Exported
    public BuildStrategy getBuildStrategy() {
        return buildStrategy;
    }

    @Exported
    public void setBuildStrategy(BuildStrategy buildStrategy) {
        this.buildStrategy = buildStrategy;
    }

    @Exported
    public String getOverride_customerId() {
        return override_customerId;
    }

    @Exported
    public String getOverride_url() {
        return override_url;
    }

    @Exported
    public String getOverride_proxy() {
        return override_proxy;
    }

    @Exported
    public String getSlMvnPluginVersion() {
        return slMvnPluginVersion;
    }

    @Exported
    public void setSlMvnPluginVersion(String slMvnPluginVersion) {
        this.slMvnPluginVersion = slMvnPluginVersion;
    }

    public String getAdditionalArguments() {
        return additionalArguments;
    }

    public void setAdditionalArguments(String additionalArguments) {
        this.additionalArguments = additionalArguments;
    }

    private void copyAgentsToSlaveIfNeeded(Logger logger, CleanupManager cleanupManager) throws IOException, InterruptedException {
        if (!StringUtils.isNullOrEmpty(buildScannerJar)) {
            CustomFile customFile = new CustomFile(logger, cleanupManager, buildScannerJar);
            customFile.copyToSlave();
        }

        if (!StringUtils.isNullOrEmpty(testListenerJar)) {
            CustomFile customFile = new CustomFile(logger, cleanupManager, testListenerJar);
            customFile.copyToSlave();
        }
    }

    private String tryGetSlMvnPluginVersion(SeaLightsPluginInfo slInfo, Logger logger) {

        String recommendedVersion = this.slMvnPluginVersion;

        try {
            if (!isValidVersion(recommendedVersion)) {
                MavenPluginUpgradeManager upgradeManager = new MavenPluginUpgradeManager(slInfo, logger);
                recommendedVersion = upgradeManager.queryServerForMavenPluginVersion();
            }
        } catch (FileNotFoundException e) {
            logger.error("Error while trying to resolve Sealights maven plugin version. " +
                    "Probably the server did not found latest maven plugin version." +
                    "Skipping Sealights integration.");
        } catch (Exception e) {
            logger.error("Error while trying to resolve Sealights maven plugin version. " +
                    "'" + e.getMessage() + "'." +
                    " Skipping Sealights integration.");
        }

        return recommendedVersion;
    }

    public boolean perform(
            AbstractBuild<?, ?> build, CleanupManager cleanupManager, Logger logger,
            String pomPath, EnvVars envVars)
            throws IOException, InterruptedException, SeaLightsIllegalStateException {

        try {
            setDefaultValues(logger);

            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            Map<String, String> metadata = JenkinsUtils.createMetadataFromEnvVars(envVars);

            FilePath ws = build.getWorkspace();
            if (ws == null) {
                return true;
            }

            copyAgentsToSlaveIfNeeded(logger, cleanupManager);

            String workingDir = ws.getRemote();

            this.pomPath = getParentPomPath(build, logger, workingDir, pomPath);

            if (this.autoRestoreBuildFile) {
                tryAddRestoreBuildFilePublisher(build, logger);
            }

            SeaLightsPluginInfo slInfo = createSeaLightsPluginInfo(build, envVars, metadata, ws, additionalProps, logger);
            SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
            if (!slInfoValidator.validate(slInfo)) {
                return true;
            }

            printFields(slInfo, logger);

            configureBuildFilePublisher(build, slInfo.getBuildFilesFolders());

            String mvnPluginVersionToUse = tryGetSlMvnPluginVersion(slInfo, logger);
            if (!isValidVersion(mvnPluginVersionToUse)) {
                //Don't integrate with maven if we can't decide our maven plugin version.
                //Return true so we do it quietly.
                return true;
            }

            doMavenIntegration(logger, slInfo, mvnPluginVersionToUse);

        } catch (Exception e) {
            // for cases when trying 'Latest-Build' when not on 'Tests Only' mode.
            if (e instanceof SeaLightsIllegalStateException) {
                throw e;
            }
            logger.error("Error occurred while performing Sealights Analysis build step.", e);
        }

        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener
    ) throws IOException, InterruptedException {
        String DEFAULT_POM_PATH = "";
        Logger logger = new Logger(listener.getLogger());
        CleanupManager cleanupManager = new CleanupManager(logger);
        try {
            EnvVars envVars = build.getEnvironment(listener);
            return perform(build, cleanupManager, logger, DEFAULT_POM_PATH, envVars);
        } catch (SeaLightsIllegalStateException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    //TODO: add unit-tests for this method
    private String getParentPomPath(AbstractBuild<?, ?> build, Logger logger, String workingDir, String pomPath) {

        if (!StringUtils.isNullOrEmpty(pomPath)) {
            JenkinsUtils jenkinsUtils = new JenkinsUtils();
            pomPath = jenkinsUtils.expandPathVariable(build, pomPath);
            Path pathToPom = Paths.get(pomPath);
            if (!pathToPom.isAbsolute()) {
                pomPath = this.joinPaths(workingDir, pomPath);
            }
        } else {
            pomPath = this.joinPaths(workingDir, "pom.xml");

        }

        logger.info("Absolute path to pom file: " + pomPath);
        return pomPath;
    }

    private void doMavenIntegration(Logger logger, SeaLightsPluginInfo slInfo, String mvnPluginVersionToUse) throws IOException, InterruptedException {

        List<String> folders = Arrays.asList(slInfo.getBuildFilesFolders().split("\\s*,\\s*"));
        List<FileBackupInfo> pomFiles = getPomFiles(folders, slInfo.getBuildFilesPatterns(), logger, pomPath);

        MavenIntegrationInfo info = new MavenIntegrationInfo(
                pomFiles,
                slInfo,
                mvnPluginVersionToUse
        );
        MavenIntegration mavenIntegration = new MavenIntegration(logger, info);
        mavenIntegration.integrate();

    }

    private String joinPaths(String path1, String path2) {
        if (path2.startsWith("/") || path2.startsWith("\\")) {
            //Path2 is rooted, so it's not relative
            return path2;
        }
        return Paths.get(path1, path2).toAbsolutePath().toString();
    }

    private String getManualBuildName() {
        BuildName.ManualBuildName manual = (BuildName.ManualBuildName) buildName;
        String insertedBuildName = manual.getInsertedBuildName();
        return insertedBuildName;
    }

    private String getFinalBuildName(AbstractBuild<?, ?> build, SeaLightsPluginInfo slInfo, Logger logger) throws IllegalStateException {

        String finalBuildName = null;

        boolean hasBuildSessionId = !StringUtils.isNullOrEmpty(slInfo.getBuildSessionId());
        boolean useNullBuildName = BuildNamingStrategy.LATEST_BUILD.equals(buildName.getBuildNamingStrategy()) ||
                BuildNamingStrategy.EMPTY_BUILD.equals(buildName.getBuildNamingStrategy());
        if (!hasBuildSessionId && useNullBuildName) {
            if (!ExecutionType.TESTS_ONLY.equals(executionType)) {
                throw new SeaLightsIllegalStateException(
                        "Trying to report 'null' as 'Build Name'. This option is allowed only with execution type of '"
                                + ExecutionType.TESTS_ONLY.getDisplayName() + "'.");
            }
            return null;
        }

        if (BuildNamingStrategy.MANUAL.equals(buildName.getBuildNamingStrategy())) {
            finalBuildName = getManualBuildName();

        } else if (BuildNamingStrategy.JENKINS_UPSTREAM.equals(buildName.getBuildNamingStrategy())) {
            BuildName.UpstreamBuildName upstream = (BuildName.UpstreamBuildName) buildName;
            String upstreamProjectName = upstream.getUpstreamProjectName();
            finalBuildName = JenkinsUtils.getUpstreamBuildName(build, upstreamProjectName, logger);
        }

        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            return String.valueOf(build.getNumber());
        }

        return finalBuildName;
    }

    private SeaLightsPluginInfo createSeaLightsPluginInfo(
            AbstractBuild<?, ?> build, EnvVars envVars, Map<String, String> metadata, FilePath ws,
            Properties additionalProps, Logger logger)
            throws SeaLightsIllegalStateException {

        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        setGlobalConfiguration(logger, slInfo, additionalProps, envVars);

        slInfo.setBuildSessionId(resolveBuildSessionId(logger, slInfo, additionalProps));

        slInfo.setMetadata(metadata);

        String workingDir = ws.getRemote();
        slInfo.setEnabled(true);

        slInfo.setBuildName(getFinalBuildName(build, slInfo, logger));

        if (workspacepath != null && !"".equals(workspacepath))
            slInfo.setWorkspacepath(workspacepath);
        else
            slInfo.setWorkspacepath(workingDir);

        slInfo.setAppName(JenkinsUtils.resolveEnvVarsInString(envVars, appName));
        slInfo.setModuleName(moduleName);
        slInfo.setBranchName(JenkinsUtils.resolveEnvVarsInString(envVars, branch));
        slInfo.setFilesIncluded(filesIncluded);
        slInfo.setFilesExcluded(filesExcluded);
        slInfo.setRecursive(recursive);
        slInfo.setPackagesIncluded(packagesIncluded);
        slInfo.setPackagesExcluded(packagesExcluded);
        slInfo.setClassLoadersExcluded(classLoadersExcluded);
        slInfo.setListenerJar(testListenerJar);
        slInfo.setListenerConfigFile(testListenerConfigFile);
        slInfo.setScannerJar(buildScannerJar);
        slInfo.setBuildStrategy(buildStrategy);
        slInfo.setEnvironment(JenkinsUtils.resolveEnvVarsInString(envVars, testStage));
        slInfo.setTestStage(JenkinsUtils.resolveEnvVarsInString(envVars, testStage));
        slInfo.setLabId(JenkinsUtils.resolveEnvVarsInString(envVars, labId));
        slInfo.setLogEnabled(!(LogLevel.OFF.equals(logLevel)));
        slInfo.setLogLevel(logLevel);
        slInfo.setLogDestination(logDestination);
        slInfo.setLogFolder(logFolder);
        slInfo.setExecutionType(executionType);

        String foldersToSearch;
        String patternsToSearch;
        if (enableMultipleBuildFiles) {
            foldersToSearch = StringUtils.isNullOrEmpty(buildFilesFolders) ? workingDir : buildFilesFolders;
            patternsToSearch = StringUtils.isNullOrEmpty(buildFilesPatterns) ? "**/pom.xml" : buildFilesPatterns;
        } else {
            foldersToSearch = workingDir;
            patternsToSearch = "**/pom.xml";
        }

        slInfo.setRecursiveOnBuildFilesFolders(enableMultipleBuildFiles);
        slInfo.setBuildFilesFolders(foldersToSearch);
        slInfo.setBuildFilesPatterns(patternsToSearch);

        return slInfo;
    }

    private String resolveBuildSessionId(Logger logger, SeaLightsPluginInfo slInfo, Properties additionalProps) {

        resolveCreateBuildSessionIdProperty(slInfo, additionalProps);

        String buildSessionIdFile = (String) additionalProps.get("buildsessionidfile");

        ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();
        buildSessionId = argumentFileResolver.resolve(logger, buildSessionId, buildSessionIdFile);

        return buildSessionId;
    }

    private void resolveCreateBuildSessionIdProperty(
            SeaLightsPluginInfo slInfo, Properties additionalProps) {

        String createBuildSessionIdString = (String) additionalProps.get("createbuildsessionid");
        boolean globalCreateBuildSessionId = getDescriptor().isCreateBuildSessionId();

        if (StringUtils.isNullOrEmpty(createBuildSessionIdString)) {
            // use createBuildSessionId checkbox from global settings
            slInfo.setCreateBuildSessionId(globalCreateBuildSessionId);
        } else {
            // use override value for this step
            boolean shouldUseCreateBuildSessionId = Boolean.valueOf(createBuildSessionIdString);
            slInfo.setCreateBuildSessionId(shouldUseCreateBuildSessionId);
        }
    }

    private void setGlobalConfiguration(Logger logger, SeaLightsPluginInfo slInfo, Properties additionalProps, EnvVars envVars) {

        String tokenPropertyValue = JenkinsUtils.resolveEnvVarsInString(envVars, (String) additionalProps.get("token"));
        String tokenFilePropertyFile = JenkinsUtils.resolveEnvVarsInString(envVars, (String) additionalProps.get("tokenfile"));
        ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();

        String token = argumentFileResolver.resolve(logger, tokenPropertyValue, tokenFilePropertyFile);
        boolean usingToken = tryUseToken(logger, slInfo, token);

        if (!usingToken) {
            // set customerId
            String customer = (String) additionalProps.get("customerid");
            if (StringUtils.isNullOrEmpty(customer)) {
                customer = override_customerId;
                if (StringUtils.isNullOrEmpty(customer)) {
                    customer = getDescriptor().getCustomerId();
                }
            }
            slInfo.setCustomerId(JenkinsUtils.resolveEnvVarsInString(envVars, customer));

            // set url
            String server = (String) additionalProps.get("server");
            if (StringUtils.isNullOrEmpty(server)) {
                server = override_url;
                if (StringUtils.isNullOrEmpty(server)) {
                    server = getDescriptor().getUrl();
                }
            }
            slInfo.setServerUrl(JenkinsUtils.resolveEnvVarsInString(envVars, server));

            boolean noCustomerOrServer = StringUtils.isNullOrEmpty(customer) || StringUtils.isNullOrEmpty(server);
            if (noCustomerOrServer) {
                throw new RuntimeException(
                        "Invalid configuration. " +
                                "Should provide 'server url' and 'customer id' when token is not provided. " +
                                "'customerId': '" + customer + "', 'server': '" + server + "'");
            }
        }

        // set proxy
        String proxy = (String) additionalProps.get("proxy");
        if (StringUtils.isNullOrEmpty(proxy)) {
            proxy = override_proxy;
            if (StringUtils.isNullOrEmpty(proxy)) {
                proxy = getDescriptor().getProxy();
            }
        }
        slInfo.setProxy(proxy);

        String filesstorage = resolveFilesStorage(additionalProps, envVars);
        slInfo.setFilesStorage(filesstorage);
    }

    private String resolveFilesStorage(Properties additionalProps, EnvVars envVars) {
        String filesStorage = (String) additionalProps.get("filesstorage");
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return JenkinsUtils.resolveEnvVarsInString(envVars, filesStorage);
        }

        filesStorage = getDescriptor().getFilesStorage();
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return filesStorage;
        }

        return System.getProperty("java.io.tmpdir");
    }

    private boolean tryUseToken(Logger logger, SeaLightsPluginInfo slInfo, String tokenPropertyValue) {
        try {
            String token = tokenPropertyValue;
            if (StringUtils.isNullOrEmpty(token)) {
                token = getDescriptor().getToken();
                if (StringUtils.isNullOrEmpty(token)) {
                    logger.warning("Sealights token is not set. Sealights will try to run without it.");
                    return false;
                }
            }

            boolean isValidToken = validateAndTryUseToken(logger, token, slInfo);
            if (!isValidToken) {
                logger.error("The provided token is invalid. Sealights will try to run without it.");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed to use token. Error: ", e);
            return false;
        }
    }

    private boolean validateAndTryUseToken(Logger logger, String token, SeaLightsPluginInfo slInfo) {
        TokenData tokenData;
        try {
            tokenData = TokenData.parse(token);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid token. Error: ", e);
            return false;
        }

        TokenValidator tokenValidator = new TokenValidator();
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);
        if (validationErrors.size() > 0) {
            logger.error("Invalid token. The token contains the following errors:");
            for (ValidationError validationError : validationErrors) {
                logger.error("Field: '" + validationError.getName() + "', Error: '" + validationError.getProblem() + "'.");
            }
            return false;
        }

        slInfo.setTokenData(tokenData);
        return true;
    }

    private List<FileBackupInfo> getPomFiles(List<String> folders, String patterns, Logger logger, String pomPath) throws IOException, InterruptedException {
        List<FileBackupInfo> pomFiles = new ArrayList<>();
        boolean isParentPomInList = false;
        VirtualChannel channel = Computer.currentComputer().getChannel();
        if (!patterns.startsWith("**" + File.separator))
            patterns = "**" + File.separator + patterns;

        for (String folder : folders) {
            List<String> remotePoms = new FilePath(channel, folder).act(new SearchFileCallable(patterns));
            for (String matchingPom : remotePoms) {
                logger.debug("Adding pom:" + matchingPom);
                if (matchingPom.equalsIgnoreCase(pomPath))
                    isParentPomInList = true;
                pomFiles.add(new FileBackupInfo(matchingPom, null));
            }
        }


        if (!isParentPomInList) {
            pomFiles.add(new FileBackupInfo(pomPath, null));
        }

        return pomFiles;
    }

    private void tryAddRestoreBuildFilePublisher(AbstractBuild build, Logger logger) {
        DescribableList publishersList = build.getProject().getPublishersList();
        boolean found = false;
        for (Object item : publishersList) {
            if (item.toString().contains("RestoreBuildFile")) {
                found = true;
                logger.debug("There was no need to add a new RestoreBuildFile since there is one. Current one:" + item.toString());
                logger.debug("Updating RestoreBuildFile.parentPomFile");
                ((RestoreBuildFile) item).setParentPomFile(pomPath);
                //If found, this was added manually. Remove the check box.
                break;
            }
        }

        if (!found) {
            RestoreBuildFile restoreBuildFile = new RestoreBuildFile(true, buildFilesFolders, pomPath);
            publishersList.add(restoreBuildFile);
        }
    }

    private void configureBuildFilePublisher(AbstractBuild build, String foldersToSearch) {
        DescribableList publishersList = build.getProject().getPublishersList();
        for (Object item : publishersList) {
            if (item.toString().contains("RestoreBuildFile")) {
                ((RestoreBuildFile) item).setFolders(foldersToSearch);
                return;
            }
        }
    }

    private void printFields(SeaLightsPluginInfo slInfo, Logger logger) {
        logger.debug("--------------Sealights Jenkins Plugin Configuration--------------");
        logger.debug("Plugin Version:" + getPluginVersion());

        printSLInfo(slInfo, logger);

        logger.debug("Enable Multiple Build Files: " + enableMultipleBuildFiles);
        logger.debug("Multiple Build Files: " + multipleBuildFiles);
        logger.debug("Override Jars: " + overrideJars);
        logger.debug("Pom Path:" + pomPath);
        logger.debug("Build Naming Strategy (from selection): " + buildName.getBuildNamingStrategy());
        logger.debug("Auto Restore Build File:" + autoRestoreBuildFile);
        logger.debug("Arguments for the Sealights Maven Plugin Installation:" + sealightsMavenPluginInstallationArguments);


        logger.debug("--------------Sealights Jenkins Plugin Configuration--------------");
    }

    private void printSLInfo(SeaLightsPluginInfo slInfo, Logger logger) {
        List<Method> getters = ReflectionUtils.getGettersMethods(slInfo);
        for (Method method : getters) {
            String methodName = method.getName();
            Object value;
            try {
                value = method.invoke(slInfo);
                logger.debug(methodName + " : " + value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error while trying to print method: " + methodName, e);
            }
        }
    }

    private static boolean isValidVersion(String v) {
        return v != null && v.matches("[0-9]+(\\.[0-9]+)*");
    }

    private String getPluginVersion() {
        return BeginAnalysis.class.getPackage().getImplementationVersion();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }


    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        private String token;
        private String customerId;
        private String url;
        private String proxy;
        private String filesStorage;
        private boolean createBuildSessionId;
        private String toolsPathOnMaster;
        private final String DEFAULT_TOOLS_PATH = "/var/lib/jenkins/tools";

        // TODO: this is for testing. need to find more elegant way to mock.
        public DescriptorImpl(boolean b) {

        }

        public DescriptorImpl() {
            super(BeginAnalysis.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public synchronized void load() {
            if (latestConfigurationExist()) {
                super.load();
                return;
            }
            tryLoadOldConfiguration();
        }

        private synchronized boolean latestConfigurationExist() {
            XmlFile latestConfigXml = getConfigFile();
            return latestConfigXml.exists();
        }

        private synchronized void tryLoadOldConfiguration() {
            XStream2 xs = new XStream2();
            xs.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BeginAnalysis$DescriptorImpl", DescriptorImpl.class);
            XmlFile oldConfigXml = new XmlFile(xs, new File(Jenkins.getInstance().getRootDir(), "io.sealigths.plugins.sealightsjenkins.BeginAnalysis.xml"));
            if (oldConfigXml.exists()) {
                try {
                    // Load old configuration xml into this object ('DescriptorImpl').
                    oldConfigXml.unmarshal(this);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to load " + oldConfigXml, e);
                }
            }
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            token = json.getString("token");
            customerId = json.getString("customerId");
            url = json.getString("url");
            proxy = json.getString("proxy");
            filesStorage = json.getString("filesStorage");
            createBuildSessionId = json.getBoolean("createBuildSessionId");
            toolsPathOnMaster = json.getString("toolsPathOnMaster");
            save();
            return super.configure(req, json);
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUrl() {
            return url;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }

        public String getFilesStorage() {
            return filesStorage;
        }

        public void setFilesStorage(String filesStorage) {
            this.filesStorage = filesStorage;
        }

        public boolean isCreateBuildSessionId() {
            return createBuildSessionId;
        }

        public void setCreateBuildSessionId(boolean createBuildSessionId) {
            this.createBuildSessionId = createBuildSessionId;
        }

        public FormValidation doCheckPackagesIncluded(@QueryParameter String packagesIncluded, @QueryParameter String buildSessionId, @QueryParameter String additionalArguments) {
            return validateBuildSessionDataParameter("Monitored Application Packages", packagesIncluded, buildSessionId, additionalArguments);
        }

        public FormValidation doCheckAppName(
                @QueryParameter String appName, @QueryParameter String buildSessionId, @QueryParameter String additionalArguments) {
            return validateBuildSessionDataParameter("App Name", appName, buildSessionId, additionalArguments);
        }

        public FormValidation doCheckBranch(@QueryParameter String branch, @QueryParameter String buildSessionId, @QueryParameter String additionalArguments) {
            return validateBuildSessionDataParameter("Branch Name", branch, buildSessionId, additionalArguments);
        }

        private FormValidation validateBuildSessionDataParameter(
                String parameterName, String parameterValue, String buildSessionId, String additionalArguments) {
            boolean buildSessionIdProvided = isBuildSessionIdProvided(buildSessionId, additionalArguments);
            if (buildSessionIdProvided || !StringUtils.isNullOrEmpty(parameterValue))
                return FormValidation.ok();
            return FormValidation.error(parameterName + " is mandatory when Build Session Id is not provided.");
        }

        public FormValidation doCheckSlMvnPluginVersion(@QueryParameter String slMvnPluginVersion) {
            if (!StringUtils.isNullOrEmpty(slMvnPluginVersion) && !isValidVersion(slMvnPluginVersion))
                return FormValidation.error("Version should be in the format of 'X.X.X'. e.g. '1.2.124'");
            return FormValidation.ok();
        }

        public DescriptorExtensionList<BuildName, BuildName.BuildNameDescriptor> getBuildNameDescriptorList() {
            return Jenkins.getInstance().getDescriptorList(BuildName.class);
        }

        private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(DescriptorImpl.class.getName());

        public String getToolsPathOnMaster() {
            if (StringUtils.isNullOrEmpty(toolsPathOnMaster))
                return DEFAULT_TOOLS_PATH;
            return toolsPathOnMaster;
        }

        public void setToolsPathOnMaster(String toolsPathOnMaster) {
            this.toolsPathOnMaster = toolsPathOnMaster;
        }

        public boolean isBuildSessionIdProvided(String buildSessionId, String additionalArguments) {
            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            boolean hasBuildSessionId = !StringUtils.isNullOrEmpty(buildSessionId);
            boolean hasBuildSessionIdFile = !StringUtils.isNullOrEmpty((String) additionalProps.get("buildsessionidfile"));

            return hasBuildSessionId || hasBuildSessionIdFile;
        }
    }
}
