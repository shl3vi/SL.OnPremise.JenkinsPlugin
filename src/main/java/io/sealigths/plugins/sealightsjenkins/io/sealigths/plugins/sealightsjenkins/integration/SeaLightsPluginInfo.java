package io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration;

/**
 * Created by Nadav on 4/19/2016.
 */
public class SeaLightsPluginInfo {
    private boolean isEnabled;
    private String appName;
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
}

