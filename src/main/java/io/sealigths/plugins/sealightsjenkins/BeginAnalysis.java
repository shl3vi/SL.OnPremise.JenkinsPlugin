package io.sealigths.plugins.sealightsjenkins;

import hudson.*;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import hudson.util.FormValidation;
import io.sealigths.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealigths.plugins.sealightsjenkins.integration.JarsHelper;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegration;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealigths.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealigths.plugins.sealightsjenkins.utils.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by shahar on 5/9/2016.
 */
@ExportedBean
public class BeginAnalysis extends Builder {

    private String appName;
    private String moduleName;
    private String branch;
    private boolean enableMultipleBuildFiles;
    private boolean overrideJars;
    private boolean multipleBuildFiles;
    private String environment;
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
    private String apiJar;
    private String testListenerConfigFile;
    private boolean autoRestoreBuildFile;
    private String buildFilesPatterns;
    private String buildFilesFolders;
    private boolean logEnabled;
    private String logFolder;
    private LogDestination logDestination = LogDestination.CONSOLE;
    private TestingFramework testingFramework = TestingFramework.AUTO_DETECT;
    private LogLevel logLevel = LogLevel.OFF;
    private BuildStrategy buildStrategy = BuildStrategy.ONE_BUILD;
    private BuildName buildName;
    private ExecutionType executionType = ExecutionType.FULL;

    private String override_customerId;
    private String override_url;
    private String override_proxy;

    @DataBoundConstructor
    public BeginAnalysis(LogLevel logLevel,
                         String appName, String moduleName, String branch, boolean enableMultipleBuildFiles,
                         boolean overrideJars, boolean multipleBuildFiles, String environment,
                         String packagesIncluded, String packagesExcluded, String filesIncluded,
                         String filesExcluded, String classLoadersExcluded, boolean recursive,
                         String workspacepath, String buildScannerJar, String testListenerJar, String apiJar,
                         String testListenerConfigFile, boolean autoRestoreBuildFile,
                         String buildFilesPatterns, String buildFilesFolders,
                         boolean logEnabled, LogDestination logDestination, String logFolder,
                         TestingFramework testingFramework, BuildStrategy buildStrategy,
                         BuildName buildName, ExecutionType executionType,
                         String override_customerId, String override_url, String override_proxy) throws IOException {

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
        this.environment = environment;
        this.testingFramework = testingFramework;
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
        this.apiJar = apiJar;
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
                e.printStackTrace();
            }
        }
    }

    private void setDefaultValues(Logger logger) {

        if (this.logDestination == null)
            this.logDestination = LogDestination.CONSOLE;

        if (this.testingFramework == null)
            this.testingFramework = TestingFramework.AUTO_DETECT;

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

    @Exported
    public String getEnvironment() {
        return environment;
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
    public String getApiJar() {
        return apiJar;
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
    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    @Exported
    public void setTestingFramework(TestingFramework testingFramework) {
        this.testingFramework = testingFramework;
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

    private String handleApiJar(Logger logger, String tmpApiJar, CleanupManager cleanupManager) throws IOException, InterruptedException {

        boolean deleteApiJarOnExit = false;
        if (org.apache.commons.lang.StringUtils.isBlank(tmpApiJar)) {
            //The user didn't specify a specific version of the test listener. Use an embedded one.
            tmpApiJar = JarsHelper.loadJarAndSaveAsTempFile("sl-api");
            deleteApiJarOnExit = true;
        } else {
            logger.info("The user specified a version for the 'apiJar'. Overriding embedded version with:'" + apiJar + "'");
        }

        if (!StringUtils.isNullOrEmpty(tmpApiJar)) {
            CustomFile customFile = new CustomFile(logger, cleanupManager, tmpApiJar);
            customFile.copyToSlave(deleteApiJarOnExit);
        }

        return tmpApiJar;

    }

    private void copyAgentsToSlaveIfNeeded(Logger logger, CleanupManager cleanupManager) throws IOException, InterruptedException {
        if (!StringUtils.isNullOrEmpty(buildScannerJar)) {
            CustomFile customFile = new CustomFile(logger, cleanupManager, buildScannerJar);
            customFile.copyToSlave(false);
        }

        if (!StringUtils.isNullOrEmpty(testListenerJar)) {
            CustomFile customFile = new CustomFile(logger, cleanupManager, testListenerJar);
            customFile.copyToSlave(false);
        }
    }

    public boolean perform(
            AbstractBuild<?, ?> build, CleanupManager cleanupManager, Logger logger, String pomPath)
	    throws IOException, InterruptedException {

        try {
            setDefaultValues(logger);

            FilePath ws = build.getWorkspace();
            if (ws == null) {
                return true;
            }

            copyAgentsToSlaveIfNeeded(logger, cleanupManager);
            String tmpApiJar = apiJar;
            tmpApiJar = handleApiJar(logger, tmpApiJar, cleanupManager);

            String workingDir = ws.getRemote();

            this.pomPath = getParentPomPath(logger, workingDir, pomPath);

            if (this.autoRestoreBuildFile) {
                tryAddRestoreBuildFilePublisher(build, logger);
            }

            SeaLightsPluginInfo slInfo = createSeaLightsPluginInfo(build, ws, logger, tmpApiJar);

            printFields(slInfo, logger);

            configureBuildFilePublisher(build, slInfo.getBuildFilesFolders());

            doMavenIntegration(logger, slInfo);

        }catch(Exception e){
            logger.error("Error occurred while performing Sealights Analysis build step.", e);
        }

        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener
    ) throws IOException, InterruptedException {
        Logger logger = new Logger(listener.getLogger());
        CleanupManager cleanupManager = new CleanupManager(logger);
        return perform(build, cleanupManager, logger, "");
    }

    private String getParentPomPath(Logger logger, String workingDir, String pomPath) {
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
        }

        logger.info("Absolute path to pom file: " + pomPath);
        return pomPath;
    }

    private String getBuildNumberFromUpstreamBuild(List<Cause> causes, String trigger) {
        String buildNum = null;
        for (Cause c : causes) {
            if (c instanceof Cause.UpstreamCause) {
                buildNum = checkCauseRecursivelyForBuildNumber((Cause.UpstreamCause) c, trigger);
                if (!StringUtils.isNullOrEmpty(buildNum)) {
                    break;
                }
            }
        }
        return buildNum;
    }

    private String checkCauseRecursivelyForBuildNumber(Cause.UpstreamCause cause, String trigger) {
        if (trigger.equals(cause.getUpstreamProject())) {
            return String.valueOf(cause.getUpstreamBuild());
        }

        return getBuildNumberFromUpstreamBuild(cause.getUpstreamCauses(), trigger);
    }

    private void doMavenIntegration(Logger logger, SeaLightsPluginInfo slInfo) throws IOException, InterruptedException {

        List<String> folders = Arrays.asList(slInfo.getBuildFilesFolders().split("\\s*,\\s*"));
        List<FileBackupInfo> pomFiles = getPomFiles(folders, slInfo.getBuildFilesPatterns(), logger, pomPath);

        MavenIntegrationInfo info = new MavenIntegrationInfo(
                pomFiles,
                slInfo,
                testingFramework
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

    private String getUpstreamBuildName(AbstractBuild<?, ?> build, Logger logger) {
        BuildName.UpstreamBuildName upstream = (BuildName.UpstreamBuildName) buildName;
        String upstreamProjectName = upstream.getUpstreamProjectName();
        String finalBuildName = getBuildNumberFromUpstreamBuild(build.getCauses(), upstreamProjectName);
        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            logger.warning("Couldn't find build number for " + upstreamProjectName + ". Using this job's build name.");
            return null;
        }

        logger.info("Upstream project: " + upstreamProjectName + " # " + finalBuildName);
        return finalBuildName;
    }

    private String getFinalBuildName(AbstractBuild<?, ?> build, Logger logger) {
        String finalBuildName = null;
        if (BuildNamingStrategy.MANUAL.equals(buildName.getBuildNamingStrategy())) {
            finalBuildName = getManualBuildName();
        } else if (BuildNamingStrategy.JENKINS_UPSTREAM.equals(buildName.getBuildNamingStrategy())) {
            finalBuildName = getUpstreamBuildName(build, logger);
        }

        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            return String.valueOf(build.getNumber());
        }

        return finalBuildName;
    }

    private SeaLightsPluginInfo createSeaLightsPluginInfo(
            AbstractBuild<?, ?> build, FilePath ws, Logger logger, String tmpApiJar) {

        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        setGlobalConfiguration(slInfo);

        String workingDir = ws.getRemote();
        slInfo.setEnabled(true);

        slInfo.setBuildName(getFinalBuildName(build, logger));

        if (workspacepath != null && !"".equals(workspacepath))
            slInfo.setWorkspacepath(workspacepath);
        else
            slInfo.setWorkspacepath(workingDir);

        slInfo.setAppName(appName);
        slInfo.setModuleName(moduleName);
        slInfo.setBranchName(branch);
        slInfo.setFilesIncluded(filesIncluded);
        slInfo.setFilesExcluded(filesExcluded);
        slInfo.setRecursive(recursive);
        slInfo.setPackagesIncluded(packagesIncluded);
        slInfo.setPackagesExcluded(packagesExcluded);
        slInfo.setClassLoadersExcluded(classLoadersExcluded);
        slInfo.setListenerJar(testListenerJar);
        slInfo.setListenerConfigFile(testListenerConfigFile);
        slInfo.setScannerJar(buildScannerJar);
        slInfo.setApiJar(tmpApiJar);
        slInfo.setBuildStrategy(buildStrategy);
        slInfo.setEnvironment(environment);
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

    private void setGlobalConfiguration(SeaLightsPluginInfo slInfo) {

        if (StringUtils.isNullOrEmpty(override_customerId)) {
            slInfo.setCustomerId(getDescriptor().getCustomerId());
        } else {
            slInfo.setCustomerId(override_customerId);
        }

        if (StringUtils.isNullOrEmpty(override_url)) {
            slInfo.setServerUrl(getDescriptor().getUrl());
        } else {
            slInfo.setServerUrl(override_url);
        }

        if (StringUtils.isNullOrEmpty(override_proxy)) {
            slInfo.setProxy(getDescriptor().getProxy());
        } else {
            slInfo.setProxy(override_proxy);
        }

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

        ReflectionUtils.printGetters(slInfo, logger);

        logger.debug("Enable Multiple Build Files: " + enableMultipleBuildFiles);
        logger.debug("Multiple Build Files: " + multipleBuildFiles);
        logger.debug("Override Jars: " + overrideJars);
        logger.debug("Pom Path:" + pomPath);
        logger.debug("Testing Framework: " + testingFramework);
        logger.debug("Build Naming Strategy (from selection): " + buildName.getBuildNamingStrategy());
        logger.debug("Auto Restore Build File:" + autoRestoreBuildFile);

        logger.debug("--------------Sealights Jenkins Plugin Configuration--------------");
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
            return true;
        }

        private String customerId;
        private String url;
        private String proxy;

        public DescriptorImpl() {
            super(BeginAnalysis.class);
            load();

        }

        @Override
        public String getDisplayName() {
            return "";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            customerId = json.getString("customerId");
            url = json.getString("url");
            proxy = json.getString("proxy");
            save();
            return super.configure(req, json);
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

        public FormValidation doCheckPackagesIncluded(@QueryParameter String packagesIncluded) {
            if (StringUtils.isNullOrEmpty(packagesIncluded))
                return FormValidation.error("Monitored Application Packages is mandatory.");
            return FormValidation.ok();
        }

        public FormValidation doCheckAppName(@QueryParameter String appName) {
            if (StringUtils.isNullOrEmpty(appName))
                return FormValidation.error("App Name is mandatory.");
            return FormValidation.ok();
        }

        public FormValidation doCheckBranch(@QueryParameter String branch) {
            if (StringUtils.isNullOrEmpty(branch))
                return FormValidation.error("Branch Name is mandatory.");
            return FormValidation.ok();
        }

        public DescriptorExtensionList<BuildName, BuildName.BuildNameDescriptor> getBuildNameDescriptorList() {
            return Hudson.getInstance().getDescriptorList(BuildName.class);
        }
    }
}
