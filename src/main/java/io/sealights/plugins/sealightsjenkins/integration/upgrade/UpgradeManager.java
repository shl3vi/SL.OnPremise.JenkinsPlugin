package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.EnvVars;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeResponse;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by shahar on 8/16/2016.
 */
public class UpgradeManager {

    public static String queryServerForMavenPluginVersion(SeaLightsPluginInfo slInfo, EnvVars envVars, Logger logger) throws IOException {
        URL url = createUrlToGetRecommendedVersion(slInfo, envVars, logger);
        ObjectMapper mapper = new ObjectMapper();
        logger.debug("Sending request to get recommended version: '"+url+"'");
        UpgradeResponse upgradeResponse = mapper.readValue(url, UpgradeResponse.class);
        return upgradeResponse.getAgent().getVersion();
    }

    private static URL createUrlToGetRecommendedVersion(SeaLightsPluginInfo slInfo, EnvVars envVars, Logger logger) throws MalformedURLException, UnsupportedEncodingException {
        String urlStr = slInfo.getServerUrl()+"/"+getBaseUrl()+"/"+getQueryString(slInfo, envVars, logger);
        return new URL(urlStr);
    }

    private static String getBaseUrl() {
        return "v1/agents/sl-maven-plugin/recommended";
    }

    private static String getQueryString(SeaLightsPluginInfo slInfo, EnvVars envVars, Logger logger) throws UnsupportedEncodingException {
        String customerId = JenkinsUtils.tryGetEnvVariable(envVars, slInfo.getCustomerId());
        String appName = JenkinsUtils.tryGetEnvVariable(envVars, slInfo.getAppName());
        String branch = JenkinsUtils.tryGetEnvVariable(envVars, slInfo.getBranchName());
        String envName = JenkinsUtils.tryGetEnvVariable(envVars, slInfo.getEnvironment());

        customerId = encodeValue(customerId);
        appName = encodeValue(appName);
        branch = encodeValue(branch);
        envName = encodeValue(envName);

        StringBuilder queryString = new StringBuilder();
        logger.debug(slInfo.getAppName());
        logger.debug(slInfo.getAppName());
        logger.debug(slInfo.getAppName());
        logger.debug(slInfo.getAppName());
        logger.debug(slInfo.getAppName());
        addQueryStringValue(queryString, "customerId", customerId);
        addQueryStringValue(queryString, "appName", appName);
        addQueryStringValue(queryString, "branch", branch);
        addQueryStringValue(queryString, "envName", envName);

        String qs = queryString.toString();
        if ("".equals(qs))
            return "";

        qs = "?" + qs;
        qs = qs.substring(0, qs.length() - 1); //Remove the last &.
        return qs;
    }

    private static void addQueryStringValue(StringBuilder queryString, String key, String value) {
        if (isNotNullOrEmpty(value) && isNotNullOrEmpty(key)) {
            queryString.append(key);
            queryString.append("=");
            queryString.append(value);
            queryString.append("&");
        }
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    private static boolean isNotNullOrEmpty(String s) {
        return !isNullOrEmpty(s);
    }

    private static String encodeValue(String value) throws UnsupportedEncodingException {
        if (isNotNullOrEmpty(value)) {
            return URLEncoder.encode(value, "UTF-8");
        }
        return null;
    }
}
