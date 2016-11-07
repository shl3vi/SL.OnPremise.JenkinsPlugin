package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeResponse;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.UrlBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UpgradeProxy {

    protected UpgradeConfiguration upgradeConfiguration;
    private Logger logger;

    public UpgradeProxy(UpgradeConfiguration upgradeConfiguration, Logger logger) {
        this.upgradeConfiguration = upgradeConfiguration;
        this.logger = logger;
    }

    public UpgradeResponse getRecommendedVersion(String componentName) throws IOException {
        URL url = createUrlToGetRecommendedVersion(componentName);
        ObjectMapper mapper = new ObjectMapper();
        logger.debug("Sending request to get recommended version: '"+url+"'");
        UpgradeResponse upgradeResponse = mapper.readValue(url, UpgradeResponse.class);
        return upgradeResponse;
    }

    private URL createUrlToGetRecommendedVersion(String componentName) throws MalformedURLException {
        UrlBuilder urlBuilder = new UrlBuilder();
        return urlBuilder.withHost(upgradeConfiguration.getServer())
                .withPath("v1","agents",componentName,"recommended")
                .withQueryParam("customerId", upgradeConfiguration.getCustomerId())
                .withQueryParam("appName", upgradeConfiguration.getAppName())
                .withQueryParam("branch", upgradeConfiguration.getBranchName())
                .withQueryParam("envName", upgradeConfiguration.getEnvironmentName())
                .toUrl();
    }

    public boolean downloadAgent(String urlToAgent, String destFile) throws IOException {
        boolean isSuccess = true;
        logger.info("Trying to download agent from url '" + urlToAgent + "' to folder '" + destFile + "'.");
        try {
            URL agentUrl = new URL(urlToAgent);
            File agentDestination = new File(destFile);
            FileUtils.copyURLToFile(agentUrl, agentDestination);

            if (!agentDestination.exists()) {
                logger.error("Failed to download recommended agent.");
                isSuccess = false;
            }
        } catch (Exception e) {
            logger.error("Error while trying to download recommended agent. Error: " + e.getMessage());
            isSuccess = false;
        }

        return isSuccess;
    }
}
