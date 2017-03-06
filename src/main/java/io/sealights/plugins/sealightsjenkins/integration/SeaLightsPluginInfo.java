package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.BuildStrategy;
import io.sealights.plugins.sealightsjenkins.ExecutionType;
import io.sealights.plugins.sealightsjenkins.LogDestination;
import io.sealights.plugins.sealightsjenkins.LogLevel;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.util.Map;

/**
 * Created by Nadav on 4/19/2016.
 */
public class SeaLightsPluginInfo {
    private boolean isEnabled;
    private TokenData tokenData;
    private String appName;
    private String moduleName;
    private String buildName;
    private String branchName;
    private String customerId;
    private String serverUrl;
    private String filesIncluded;
    private String filesExcluded;
    private String packagesIncluded;
    private String packagesExcluded;
    private String classLoadersExcluded;
    private String workspacepath;
    private String proxy;
    private boolean recursive;

    @Deprecated
    private String environment;
    private String testStage;
    private String labId;

    private String buildFilesFolders;
    private String buildFilesPatterns;
    private boolean recursiveOnBuildFilesFolders;
    private ExecutionType executionType;
    private Map<String, String> metadata;
    private String filesStorage;

    private boolean logEnabled;
    private LogDestination logDestination;
    private LogLevel logLevel;
    private String logFolder;

    private String scannerJar;
    private String listenerJar;
    private String listenerConfigFile;

    private BuildStrategy buildStrategy;

    private String overrideTestListenerPath;
    private String overrideMetaJsonPath;

    private String buildSessionId;
    private boolean createBuildSessionId;

    public boolean isRecursiveOnBuildFilesFolders() {
        return recursiveOnBuildFilesFolders;
    }

    public void setRecursiveOnBuildFilesFolders(boolean recursiveOnBuildFilesFolders) {
        this.recursiveOnBuildFilesFolders = recursiveOnBuildFilesFolders;
    }

    public String getBuildFilesFolders() {
        return buildFilesFolders;
    }

    public void setBuildFilesFolders(String buildFilesFolders) {
        this.buildFilesFolders = buildFilesFolders;
    }

    public String getBuildFilesPatterns() {
        return buildFilesPatterns;
    }

    public void setBuildFilesPatterns(String buildFilesPatterns) {
        this.buildFilesPatterns = buildFilesPatterns;
    }

    public BuildStrategy getBuildStrategy() {
        return buildStrategy;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getWorkspacepath() {
        return workspacepath;
    }

    public void setWorkspacepath(String workspacepath) {
        this.workspacepath = workspacepath;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getFilesIncluded() {
        return filesIncluded;
    }

    public void setFilesIncluded(String filesIncluded) {
        this.filesIncluded = filesIncluded;
    }

    public String getFilesExcluded() {
        return filesExcluded;
    }

    public void setFilesExcluded(String filesExcluded) {
        this.filesExcluded = filesExcluded;
    }

    public String getPackagesIncluded() {
        return packagesIncluded;
    }

    public void setPackagesIncluded(String packagesIncluded) {
        this.packagesIncluded = packagesIncluded;
    }

    public String getPackagesExcluded() {
        return packagesExcluded;
    }

    public void setPackagesExcluded(String packagesExcluded) {
        this.packagesExcluded = packagesExcluded;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public String getScannerJar() {
        return scannerJar;
    }

    public void setScannerJar(String scannerJar) {
        this.scannerJar = scannerJar;
    }

    public String getListenerJar() {
        return listenerJar;
    }

    public void setListenerJar(String listenerJar) {
        this.listenerJar = listenerJar;
    }

    public String getListenerConfigFile() {
        return listenerConfigFile;
    }

    public void setListenerConfigFile(String listenerConfigFile) {
        this.listenerConfigFile = listenerConfigFile;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public void setLogDestination(LogDestination logDestination) {
        this.logDestination = logDestination;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public void setBuildStrategy(BuildStrategy buildStrategy) {
        this.buildStrategy = buildStrategy;
    }

    @Deprecated
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Deprecated
    public String getEnvironment() {
        return environment;
    }

    public String getTestStage() {
        if (!StringUtils.isNullOrEmpty(testStage)){
            return testStage;
        }
        if (!StringUtils.isNullOrEmpty(environment)){
            testStage = environment;
        }
        return testStage;
    }

    public void setTestStage(String testStage) {
        this.testStage = testStage;
    }

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    public LogDestination getLogDestination() {
        return logDestination;
    }

    public String getClassLoadersExcluded() {
        return classLoadersExcluded;
    }

    public void setClassLoadersExcluded(String classLoadersExcluded) {
        this.classLoadersExcluded = classLoadersExcluded;
    }

    public String getFilesStorage() {
        return this.filesStorage;
    }

    public void setFilesStorage(String filesStorage) {
        this.filesStorage = filesStorage;
    }

    public String getOverrideTestListenerPath() {
        return overrideTestListenerPath;
    }

    public void setOverrideTestListenerPath(String overrideTestListenerPath) {
        this.overrideTestListenerPath = overrideTestListenerPath;
    }

    public String getOverrideMetaJsonPath() {
        return overrideMetaJsonPath;
    }

    public void setOverrideMetaJsonPath(String overrideMetaJsonPath) {
        this.overrideMetaJsonPath = overrideMetaJsonPath;
    }

    public TokenData getTokenData() {
        return tokenData;
    }

    public void setTokenData(TokenData tokenData) {
        this.tokenData = tokenData;
    }

    public String getBuildSessionId() {
        return buildSessionId;
    }

    public void setBuildSessionId(String buildSessionId) {
        this.buildSessionId = buildSessionId;
    }

    public boolean isCreateBuildSessionId() {
        return createBuildSessionId;
    }

    public void setCreateBuildSessionId(boolean createBuildSessionId) {
        this.createBuildSessionId = createBuildSessionId;
    }
}

