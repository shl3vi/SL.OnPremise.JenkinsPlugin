package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.TestingFramework;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private String sourcePomFiles;
    private String targetPomFile;
    private String profileId;
    private TestingFramework testingFramework;
    private SeaLightsPluginInfo seaLightsPluginInfo;


    public MavenIntegrationInfo(String sourcePomFiles, String targetPomFile, SeaLightsPluginInfo seaLightsPluginInfo, TestingFramework testingFramework) {
        this.sourcePomFiles = sourcePomFiles;
        this.targetPomFile = targetPomFile;
        this.seaLightsPluginInfo = seaLightsPluginInfo;
        this.testingFramework = testingFramework;
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

    public String getSourcePomFiles() {
        return sourcePomFiles;
    }

    public void setSourcePomFiles(String sourcePomFiles) {
        this.sourcePomFiles = sourcePomFiles;
    }

    public String getTargetPomFile() {
        return targetPomFile;
    }

    public void setTargetPomFile(String targetPomFile) {
        this.targetPomFile = targetPomFile;
    }
}
