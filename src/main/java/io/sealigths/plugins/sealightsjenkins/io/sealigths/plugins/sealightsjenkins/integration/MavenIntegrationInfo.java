package io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private String pomFilePath;
    private String profileId;
    private String testingFramework;
    private String apiAgentPath;
    private SeaLightsPluginInfo seaLightsPluginInfo;


    public String getPomFilePath() {
        return pomFilePath;
    }

    public void setPomFilePath(String pomFilePath) {
        this.pomFilePath = pomFilePath;
    }

    public SeaLightsPluginInfo getSeaLightsPluginInfo() {
        return seaLightsPluginInfo;
    }

    public void setSeaLightsPluginInfo(SeaLightsPluginInfo seaLightsPluginInfo) {
        this.seaLightsPluginInfo = seaLightsPluginInfo;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getTestingFramework() {
        return testingFramework;
    }

    public void setTestingFramework(String testingFramework) {
        this.testingFramework = testingFramework;
    }

    public String getApiAgentPath() {
        return apiAgentPath;
    }

    public void setApiAgentPath(String apiAgentPath) {
        this.apiAgentPath = apiAgentPath;
    }
}
