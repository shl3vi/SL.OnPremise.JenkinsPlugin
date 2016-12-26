package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.ComponentName;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Created by shahar on 12/26/2016.
 */
public class BuildScannerUpgradeManager extends AgentsUpgradeManager {

    public BuildScannerUpgradeManager(UpgradeProxy upgradeProxy, UpgradeConfiguration upgradeConfiguration, Logger logger) {
        super(upgradeProxy, upgradeConfiguration, logger);
    }

    @Override
    protected ComponentName getComponentNameEnum() {
        return  ComponentName.BUILD_SCANNER_COMPONENT_NAME;
    }
}
