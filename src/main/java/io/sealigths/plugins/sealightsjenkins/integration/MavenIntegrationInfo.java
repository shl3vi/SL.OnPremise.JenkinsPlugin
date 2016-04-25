package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.TestingFramework;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private String pomFilePath;
    private String profileId;
    private TestingFramework testingFramework;
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

    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    public void setTestingFramework(TestingFramework testingFramework) {
        this.testingFramework = testingFramework;
    }
}
