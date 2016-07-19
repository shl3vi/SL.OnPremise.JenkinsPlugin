package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.entities.FileBackupInfo;

import java.util.List;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private List<FileBackupInfo> pomFiles;
    private SeaLightsPluginInfo seaLightsPluginInfo;


    public MavenIntegrationInfo(List<FileBackupInfo> pomFiles, SeaLightsPluginInfo seaLightsPluginInfo) {
        this.pomFiles = pomFiles;
        this.seaLightsPluginInfo = seaLightsPluginInfo;
    }

    public SeaLightsPluginInfo getSeaLightsPluginInfo() {
        return seaLightsPluginInfo;
    }

    public void setSeaLightsPluginInfo(SeaLightsPluginInfo seaLightsPluginInfo) {
        this.seaLightsPluginInfo = seaLightsPluginInfo;
    }

    public List<FileBackupInfo> getPomFiles() {
        return pomFiles;
    }
}
