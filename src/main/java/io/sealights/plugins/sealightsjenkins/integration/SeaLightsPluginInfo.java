package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.BuildStrategy;
import io.sealights.plugins.sealightsjenkins.ExecutionType;
import io.sealights.plugins.sealightsjenkins.LogDestination;
import io.sealights.plugins.sealightsjenkins.LogLevel;

import java.util.Map;

/**
 * Created by Nadav on 4/19/2016.
 */
public class SeaLightsPluginInfo {
    private boolean isEnabled;
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
    private String environment;
    private String buildFilesFolders;
    private String buildFilesPatterns;
    private boolean recursiveOnBuildFilesFolders;
    private ExecutionType executionType;
    private Map<String, String> metadata;
    private String filesStorage;

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

    private String scannerJar;
    private String listenerJar;
    private String listenerConfigFile;

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

    private boolean logEnabled;
    private LogDestination logDestination;
    private LogLevel logLevel;
    private String logFolder;

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

    private BuildStrategy buildStrategy;

    public void setBuildStrategy(BuildStrategy buildStrategy) {
        this.buildStrategy = buildStrategy;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
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
}

