package io.sealights.plugins.sealightsjenkins;

import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

/**
 * Created by shahar on 12/25/2016.
 */
public class SlInfoValidator {

    public boolean validate(SeaLightsPluginInfo slInfo, Logger logger){
        boolean isValid = true;
        boolean isBuildSessionIdMentioned = StringUtils.isNullOrEmpty(slInfo.getBuildSessionId());
        if (slInfo.isCreateBuildSessionId() || !isBuildSessionIdMentioned){
            // make sure we have properties to create build session id.
            if (StringUtils.isNullOrEmpty(slInfo.getAppName())){
                if (!isBuildSessionIdMentioned) {
                    logger.info("Please provide 'App Name' when 'createBuildSessionId' is set to 'true'.");
                }else{
                    logger.info("'App Name' is mandatory when 'buildSessionId' is not provided.");
                }
                isValid = false;
            }
            if (StringUtils.isNullOrEmpty(slInfo.getBuildName())){
                if (!isBuildSessionIdMentioned) {
                    logger.info("Please provide 'Build Name' when 'createBuildSessionId' is set to 'true'");
                }else{
                    logger.info("'Build Name' is mandatory when 'buildSessionId' is not provided.");
                }
                isValid = false;
            }
            if (StringUtils.isNullOrEmpty(slInfo.getBranchName())){
                if (!isBuildSessionIdMentioned) {
                    logger.info("Please provide 'Branch Name' when 'createBuildSessionId' is set to 'true'");
                }else{
                    logger.info("'Branch Name' is mandatory when 'buildSessionId' is not provided.");
                }
                isValid = false;
            }
            if (StringUtils.isNullOrEmpty(slInfo.getPackagesIncluded())){
                if (!isBuildSessionIdMentioned) {
                    logger.info("Please provide 'Monitored packages' when 'CreateBuildSessionId' is set to 'true'");
                }else{
                    logger.info("'Monitored packages' is mandatory when 'buildSessionId' is not provided.");
                }
                isValid = false;
            }
        }
        return isValid;
    }
}
