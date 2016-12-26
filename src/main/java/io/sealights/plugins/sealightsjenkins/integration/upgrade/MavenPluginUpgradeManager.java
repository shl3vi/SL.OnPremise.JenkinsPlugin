package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeResponse;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

import java.io.IOException;

/**
 * This class is responsible to get SeaLights-maven-plugin recommended version
 */
public class MavenPluginUpgradeManager {

    private SeaLightsPluginInfo slInfo;
    private Logger logger;

    public MavenPluginUpgradeManager(SeaLightsPluginInfo slInfo, Logger logger) {
        this.slInfo = slInfo;
        this.logger = logger;
    }

    public String queryServerForMavenPluginVersion() throws IOException {
        UpgradeConfiguration upgradeConfiguration = createUpgradeConfiguration();
        UpgradeProxy upgradeProxy = new UpgradeProxy(upgradeConfiguration, logger);
        UpgradeResponse upgradeResponse = upgradeProxy.getRecommendedVersion("sl-maven-plugin");
        return upgradeResponse.getAgent().getVersion();
    }

    private UpgradeConfiguration createUpgradeConfiguration(){

        String token = null;
        String customerId = slInfo.getCustomerId();
        String server = slInfo.getServerUrl();

        TokenData tokenData = slInfo.getTokenData();
        if (tokenData != null){
            token = tokenData.getToken();
            customerId = tokenData.getCustomerId();
            server = tokenData.getServer();
        }

        return new UpgradeConfiguration(
                token,
                customerId,
                slInfo.getBuildSessionId(),
                slInfo.getAppName(),
                slInfo.getEnvironment(),
                slInfo.getBranchName(),
                server,
                slInfo.getProxy(),
                slInfo.getFilesStorage()
        );
    }
}
