package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.ExecutionType;
import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import io.sealigths.plugins.sealightsjenkins.utils.FileAndFolderUtils;
import io.sealigths.plugins.sealightsjenkins.utils.Logger;
import org.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;

import static io.sealigths.plugins.sealightsjenkins.utils.StringUtils.isNullOrEmpty;

/**
 * Created by Nadav on 6/2/2016.
 */
public class SealightsMavenPluginHelper {
    private final String SL_MVN_VERSION = "1.0.0";
    private Logger logger;

    public SealightsMavenPluginHelper(Logger logger)
    {
        this.logger = logger;
    }

    public String getPluginVersion() {
        String version = null;
        try {
            version = FileAndFolderUtils.readFileFromResources("sl-maven-plugin-version.txt", logger);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(version)) {
            return version;
        }

        logger.warning("Couldn't load the version number of the maven plugin from the resources. Using default version.");
        return SL_MVN_VERSION;

    }

    public String toPluginText(SeaLightsPluginInfo pluginInfo, TestingFramework testingFramework) {

        StringBuilder plugin = new StringBuilder();
        plugin.append("<groupId>io.sealights.on-premise.agents.plugin</groupId>");
        plugin.append("<artifactId>sealights-maven-plugin</artifactId>");
        plugin.append("<version>" + getPluginVersion()+"</version>");
        plugin.append("<configuration>");

        if (!pluginInfo.isEnabled()) {
            plugin.append("<enable>false</enable>");
        }

        tryAppendValue(plugin, pluginInfo.getCustomerId(), "customerid");
        tryAppendValue(plugin, pluginInfo.getServerUrl(), "server");
        tryAppendValue(plugin, pluginInfo.getProxy(), "proxy");


        String appName = pluginInfo.getAppName();
        if ("Build Per Module".equalsIgnoreCase(pluginInfo.getBuildStrategy().getDisplayName())) {
            appName = "[" + pluginInfo.getAppName() + "] - " + pluginInfo.getModuleName();
        }

        tryAppendValue(plugin, appName, "appName");
        tryAppendValue(plugin, pluginInfo.getModuleName(), "moduleName");
        tryAppendValue(plugin, pluginInfo.getWorkspacepath(), "workspacepath");
        tryAppendValue(plugin, pluginInfo.getBuildName(), "build");
        tryAppendValue(plugin, pluginInfo.getBranchName(), "branch");
        tryAppendValue(plugin, pluginInfo.getPackagesIncluded(), "packagesincluded");

        if (!isNullOrEmpty(pluginInfo.getPackagesExcluded())) {
            plugin.append("<packagesexcluded>*FastClassByGuice*, *ByCGLIB*, *EnhancerByMockitoWithCGLIB*, *EnhancerBySpringCGLIB*, " + pluginInfo.getPackagesExcluded() + "</packagesexcluded>");
        }

        if (!isNullOrEmpty(pluginInfo.getClassLoadersExcluded())) {
            plugin.append("<classLoadersExcluded>org.powermock.core.classloader.MockClassLoader, " + pluginInfo.getClassLoadersExcluded() + "</classLoadersExcluded>");
        }

        tryAppendValue(plugin, pluginInfo.getFilesIncluded(), "filesincluded");
        tryAppendValue(plugin, pluginInfo.getApiJar(), "apiJar");
        tryAppendValue(plugin, pluginInfo.getScannerJar(), "buildScannerJar");
        tryAppendValue(plugin, pluginInfo.getListenerJar(), "testListenerJar");
        tryAppendValue(plugin, pluginInfo.getListenerConfigFile(), "testListenerConfigFile");
        tryAppendValue(plugin, pluginInfo.getEnvironment(), "environment");
        tryAppendValue(plugin, pluginInfo.getFilesExcluded(), "filesexcluded");

        if (!pluginInfo.isRecursive()) {
            plugin.append("<recursive>false</recursive>");
        }

        if (pluginInfo.isLogEnabled()) {
            plugin.append("<logEnabled>true</logEnabled>");
        }
        String logLevel = pluginInfo.getLogLevel().name();
        tryAppendValue(plugin, logLevel, "logLevel");

        if (pluginInfo.getLogDestination() != null && "file".equalsIgnoreCase(pluginInfo.getLogDestination().name())) {
            plugin.append("<logToFile>true</logToFile>");
        }

        tryAppendValue(plugin, pluginInfo.getLogFolder(), "logFolder");
        plugin.append("<enableTestListenerInitialization>" + testingFramework.equals(TestingFramework.AUTO_DETECT) + "</enableTestListenerInitialization>");


        plugin.append("</configuration>");
        plugin.append("<executions>");

        boolean shouldExecuteScanner = ExecutionType.FULL.equals(pluginInfo.getExecutionType());
        if (shouldExecuteScanner)
            appendExecution(plugin, "a1", "build-scanner");
        appendExecution(plugin, "a2", "test-listener");
        appendExecution(plugin, "a3", "initialize-test-listener");
        plugin.append("</executions>");


        return plugin.toString();
    }

    private void appendExecution(StringBuilder stringBuilder, String executionId, String goal) {
        stringBuilder.append("<execution>");
        stringBuilder.append("<id>" + executionId + "</id>");
        stringBuilder.append("<goals>");
        stringBuilder.append("<goal>" + goal + "</goal>");
        stringBuilder.append("</goals>");
        stringBuilder.append("</execution>");
    }

    private void tryAppendValue(StringBuilder stringBuilder, String value, String elementName) {
        if (!isNullOrEmpty(value)) {
            stringBuilder.append("<" + elementName + ">");
            stringBuilder.append(value);
            stringBuilder.append("</" + elementName + ">");
        }
    }
}
