package io.sealigths.plugins.sealightsjenkins.integration;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private String sourcePomFile;
    private String targetPomFile;
    private String profileId;
    private String testingFramework;
    private SeaLightsPluginInfo seaLightsPluginInfo;




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

    public String getSourcePomFile() {
        return sourcePomFile;
    }

    public void setSourcePomFile(String sourcePomFile) {
        this.sourcePomFile = sourcePomFile;
    }

    public String getTargetPomFile() {
        return targetPomFile;
    }

    public void setTargetPomFile(String targetPomFile) {
        this.targetPomFile = targetPomFile;
    }
}
