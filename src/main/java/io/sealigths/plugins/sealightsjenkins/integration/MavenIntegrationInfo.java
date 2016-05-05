package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import io.sealigths.plugins.sealightsjenkins.entities.FileBackupInfo;

import java.util.List;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private List<FileBackupInfo> pomFiles;
    private TestingFramework testingFramework;
    private SeaLightsPluginInfo seaLightsPluginInfo;


    public MavenIntegrationInfo(List<FileBackupInfo> pomFiles, SeaLightsPluginInfo seaLightsPluginInfo, TestingFramework testingFramework) {
        this.pomFiles = pomFiles;
        this.seaLightsPluginInfo = seaLightsPluginInfo;
        this.testingFramework = testingFramework;
    }

    public SeaLightsPluginInfo getSeaLightsPluginInfo() {
        return seaLightsPluginInfo;
    }

    public void setSeaLightsPluginInfo(SeaLightsPluginInfo seaLightsPluginInfo) {
        this.seaLightsPluginInfo = seaLightsPluginInfo;
    }

    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    public void setTestingFramework(TestingFramework testingFramework) {
        this.testingFramework = testingFramework;
    }


    public List<FileBackupInfo> getPomFiles() {
        return pomFiles;
    }
}
