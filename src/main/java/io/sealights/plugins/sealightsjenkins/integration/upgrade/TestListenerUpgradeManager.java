package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.ComponentName;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

public class TestListenerUpgradeManager extends AgentsUpgradeManager {

    public TestListenerUpgradeManager(UpgradeProxy upgradeProxy, UpgradeConfiguration upgradeConfiguration, Logger logger) {
        super(upgradeProxy, upgradeConfiguration, logger);
    }

    @Override
    protected ComponentName getComponentNameEnum() {
        return  ComponentName.TEST_LISTENER_COMPONENT_NAME;
    }

}

