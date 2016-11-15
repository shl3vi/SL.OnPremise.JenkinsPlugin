package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.CommandMode;

/**
 * Basic arguments that is needed for the executors
 */
public class BaseCommandArguments {

    private CommandMode mode;
    private String appName;
    private String branchName;
    private String buildName;
    private String token;
    private String customerId;
    private String url;
    private String proxy;
    private String environment;
    private String agentPath;
    private String javaPath;

    public CommandMode getMode() {
        return mode;
    }

    public void setMode(CommandMode mode) {
        this.mode = mode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUrl() {
        return url;
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

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getAgentPath() {
        return agentPath;
    }

    public void setAgentPath(String agentPath) {
        this.agentPath = agentPath;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }
}
