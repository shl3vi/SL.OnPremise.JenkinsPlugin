package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Arguments for the 'externalReport' command.
 */
public class ExternalReportArguments {

    private String token;
    private String tokenFile;
    private String proxy;
    private String buildSessionId;
    private String buildSessionIdFile;
    private String appName;
    private String buildName;
    private String branchName;
    private String report;
    private BaseCommandArguments baseArgs;

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

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
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

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
    }

    public void setBaseArgs(BaseCommandArguments baseArgs) {
        this.baseArgs = baseArgs;
    }
}
