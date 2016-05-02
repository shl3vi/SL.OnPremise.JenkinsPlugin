package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.BuildStrategy;
import io.sealigths.plugins.sealightsjenkins.LogDestination;
import io.sealigths.plugins.sealightsjenkins.LogLevel;

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
    private String workspacepath;
    private String proxy;
    private boolean recursive;
    private String environment;
    private String buildFilesFolders;
    private String buildFilesPatterns;
    private boolean recursiveOnBuildFilesFolders;

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

    private String scannerJar;
    private String listenerJar;
    private String apiJar;
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

    public LogDestination isLogDestination() {
        return logDestination;
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

    public BuildStrategy isBuildStrategy() {
        return buildStrategy;
    }

    public void setBuildStrategy(BuildStrategy buildStrategy) {
        this.buildStrategy = buildStrategy;
    }

    public String toPluginText(){

        StringBuilder plugin = new StringBuilder();
        plugin.append("<groupId>io.sealights.on-premise.agents.plugin</groupId>");
        plugin.append("<artifactId>sealights-maven-plugin</artifactId>");
        plugin.append("<version>1.0.0</version>");
        plugin.append("<configuration>");

        if (!isEnabled){
            plugin.append("<enable>false</enable>");
        }

        if(!isNullOrEmpty(customerId)){
            plugin.append("<customerid>" + customerId + "</customerid>");
        }
        if(!isNullOrEmpty(serverUrl)){
            plugin.append("<server>" + serverUrl + "</server>");
        }
        if(!isNullOrEmpty(proxy)){
            plugin.append("<proxy>" + proxy + "</proxy>");
        }

        if("Build Per Module".equalsIgnoreCase(buildStrategy.getDisplayName())){
            appName = "[" + appName + "] - " +moduleName;
        }
        if(!isNullOrEmpty(appName)){
            plugin.append("<appName>" + appName + "</appName>");
        }
//        if(!isNullOrEmpty(moduleName)){
//            plugin.append("<moduleName>" + moduleName + "</moduleName>");
//        }
        if(!isNullOrEmpty(workspacepath)){
            plugin.append("<workspacepath>" + workspacepath + "</workspacepath>");
        }
        if(!isNullOrEmpty(buildName)){
            plugin.append("<build>" + buildName + "</build>");
        }
//        if(!isNullOrEmpty(environment)){
//            plugin.append("<environment>" + environment + "</environment>");
//        }
        if(!isNullOrEmpty(branchName)){
            plugin.append("<branch>" + branchName + "</branch>");
        }
        if(!isNullOrEmpty(packagesIncluded)){
            plugin.append("<packagesincluded>" + packagesIncluded + "</packagesincluded>");
        }
        if(!isNullOrEmpty(packagesExcluded)){
            plugin.append("<packagesexcluded>*FastClassByGuice*, *ByCGLIB*, *EnhancerByMockitoWithCGLIB*, *EnhancerBySpringCGLIB*, " + packagesExcluded + "</packagesexcluded>");
        }
        if(!isNullOrEmpty(filesIncluded)){
            plugin.append("<filesincluded>" + filesIncluded + "</filesincluded>");
        }
//        if(!isNullOrEmpty(filesExcluded)){
//            plugin.append("<filesexcluded>" + filesExcluded + "</filesexcluded>");
//        }
        if(!isNullOrEmpty(scannerJar)){
            plugin.append("<buildScannerJar>" + scannerJar + "</buildScannerJar>");
        }
        if(!isNullOrEmpty(listenerJar)){
            plugin.append("<testListenerJar>" + listenerJar + "</testListenerJar>");
        }
        if(!isNullOrEmpty(listenerConfigFile)){
            plugin.append("<testListenerConfigFile>" + listenerConfigFile + "</testListenerConfigFile>");
        }
        if(!recursive){
            plugin.append("<recursive>false</recursive>");
        }

        if(logEnabled){
            plugin.append("<logEnabled>true</logEnabled>");
        }
        if(!isNullOrEmpty(logLevel.name())){
            plugin.append("<logLevel>" + logLevel.name() + "</logLevel>");
        }
        if(logDestination != null && "file".equalsIgnoreCase(logDestination.name())){
            plugin.append("<logToFile>true</logToFile>");
        }
        if(!isNullOrEmpty(logFolder)){
            plugin.append("<logFolder>" + logFolder + "</logFolder>");
        }


        plugin.append("</configuration>");
        plugin.append("<executions>");
        plugin.append("<execution>");


//        if ("One Build".equalsIgnoreCase(buildStrategy.getDisplayName()))
//            plugin.append("<inherited>false</inherited>");

        plugin.append("<id>a1</id>");
        plugin.append("<goals>");
        plugin.append("<goal>build-scanner</goal>");
        plugin.append("</goals>");

        plugin.append("</execution>");
        plugin.append("<execution>");
        plugin.append("<id>a2</id>");
        plugin.append("<goals>");
        plugin.append("<goal>test-listener</goal>");
        plugin.append("</goals>");
        plugin.append("</execution>");
        plugin.append("</executions>");


        return plugin.toString();
    }

    private boolean isNullOrEmpty(String str){
        if (str == null || "".equals(str))
            return true;
        return false;
    }

    public String getApiJar() {
        return apiJar;
    }

    public void setApiJar(String apiJar) {
        this.apiJar = apiJar;
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
}

