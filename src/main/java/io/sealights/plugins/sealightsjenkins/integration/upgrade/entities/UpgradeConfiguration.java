package io.sealights.plugins.sealightsjenkins.integration.upgrade.entities;

public class UpgradeConfiguration {
    private String token;
    private String customerId;
    private String appName;
    private String environmentName;
    private String branchName;
    private String server;
    private String proxy;
    private String filesStorage;

    public UpgradeConfiguration(String token, String customerId, String appName,
                                String environmentName, String branchName, String server,
                                String proxy, String filesStorage) {
        this.token = token;
        this.customerId = customerId;
        this.appName = appName;
        this.environmentName = environmentName;
        this.branchName = branchName;
        this.server = server;
        this.proxy = proxy;
        this.filesStorage = filesStorage;
    }

    public String getToken() {
        return token;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAppName() {
        return appName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getServer() {
        return server;
    }

    public String getProxy() {
        return proxy;
    }

    public String getFilesStorage() {
        return filesStorage;
    }

}

