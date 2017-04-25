package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Basic arguments that is needed for the executors
 */
public class BaseCommandArguments {

    private String appName;
    private String branchName;
    private String buildName;

    private String token;
    private String tokenFile;
    private TokenData tokenData;

    private String customerId;
    private String url;
    private String proxy;
    private String labId;
    private String agentPath;
    private String javaPath;

    private String buildSessionId;
    private String buildSessionIdFile;

    private AbstractBuild<?, ?> build;
    private EnvVars envVars;
    private Logger logger;

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

    public TokenData getTokenData() {
        return tokenData;
    }

    public void setTokenData(TokenData tokenData) {
        this.tokenData = tokenData;
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

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenFile() {
        return tokenFile;
    }

    public void setTokenFile(String tokenFile) {
        this.tokenFile = tokenFile;
    }

    public String getBuildSessionId() {
        return buildSessionId;
    }

    public void setBuildSessionId(String buildSessionId) {
        this.buildSessionId = buildSessionId;
    }

    public String getBuildSessionIdFile() {
        return buildSessionIdFile;
    }

    public void setBuildSessionIdFile(String buildSessionIdFile) {
        this.buildSessionIdFile = buildSessionIdFile;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public void setBuild(AbstractBuild<?, ?> build) {
        this.build = build;
    }

    public EnvVars getEnvVars() {
        return envVars;
    }

    public void setEnvVars(EnvVars envVars) {
        this.envVars = envVars;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String toString() {
        String tokenHint = "..."+token.substring(token.length() - 10);
        return "BaseCommandArguments{" +
                "appName='" + appName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", buildName='" + buildName + '\'' +
                ", token='" + tokenHint + '\'' +
                ", tokenFile='" + tokenFile + '\'' +
                ", tokenData=" + tokenData +
                ", customerId='" + customerId + '\'' +
                ", url='" + url + '\'' +
                ", proxy='" + proxy + '\'' +
                ", labId='" + labId + '\'' +
                ", agentPath='" + agentPath + '\'' +
                ", javaPath='" + javaPath + '\'' +
                ", buildSessionId='" + buildSessionId + '\'' +
                ", buildSessionIdFile='" + buildSessionIdFile + '\'' +
                '}';
    }
}
