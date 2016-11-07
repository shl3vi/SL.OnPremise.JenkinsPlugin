package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

public abstract class AgentsUpgradeManager extends AbstractUpgradeManager {

    public AgentsUpgradeManager(UpgradeProxy upgradeProxy, UpgradeConfiguration upgradeConfiguration, Logger logger) {
        super(upgradeProxy, upgradeConfiguration, logger);
    }

    @Override
    protected String getFileToDownloadName() {
        return "sealights-java";
    }
}

