package io.sealights.plugins.sealightsjenkins;

import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

/**
 * Created by shahar on 12/25/2016.
 */
public class SlInfoValidator {

    private Logger logger;

    public SlInfoValidator(Logger logger) {
        this.logger = logger;
    }

    public boolean validate(SeaLightsPluginInfo slInfo) {
        boolean isValid = true;
        boolean hasBuildSessionId = !StringUtils.isNullOrEmpty(slInfo.getBuildSessionId());
        if (slInfo.isCreateBuildSessionId() || !hasBuildSessionId) {
            // make sure we have properties to create build session id.
            isValid = validateField(slInfo.getAppName(), !hasBuildSessionId, "App Name");
            isValid = isValid && validateField(slInfo.getBuildName(), !hasBuildSessionId, "Build Name");
            isValid = isValid && validateField(slInfo.getBranchName(), !hasBuildSessionId, "Branch Name");
            isValid = isValid && validateField(slInfo.getPackagesIncluded(), !hasBuildSessionId, "Monitored packages");
        }

        return isValid;
    }

    private boolean validateField(String value, boolean hasBuildSessionId, String name) {
        if (StringUtils.isNullOrEmpty(value)) {
            if (!hasBuildSessionId) {
                logger.info("Please provide '" + name + "' when 'createBuildSessionId' is set to 'true'.");
            } else {
                logger.info("'" + name + "' is mandatory when 'buildSessionId' is not provided.");
            }
            return false;
        }
        return true;
    }
}
