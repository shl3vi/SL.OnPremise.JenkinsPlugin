package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.entities.FileBackupInfo;

import java.util.List;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegrationInfo {
    private List<FileBackupInfo> pomFiles;
    private SeaLightsPluginInfo seaLightsPluginInfo;
    private String overridePluginVersion;

    public MavenIntegrationInfo(List<FileBackupInfo> pomFiles, SeaLightsPluginInfo seaLightsPluginInfo
            , String overridePluginVersion) {
        this.pomFiles = pomFiles;
        this.seaLightsPluginInfo = seaLightsPluginInfo;
        this.overridePluginVersion = overridePluginVersion;
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

    public String getOverridePluginVersion() {
        return overridePluginVersion;
    }
}
