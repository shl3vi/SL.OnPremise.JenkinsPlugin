package io.sealights.plugins.sealightsjenkins.integration.plugins;

import io.sealights.plugins.sealightsjenkins.LogLevel;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.utils.PathUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.sealights.plugins.sealightsjenkins.utils.StringUtils.isNullOrEmpty;

/**
 * Created by shahar on 8/31/2016.
 */
public class PluginsUtils {

    public static List<String> createSLArgumentList(SeaLightsPluginInfo pluginInfo, String logFolderName)
            throws IOException, SAXException, ParserConfigurationException {

        List<String> argumentList = new ArrayList<>();

        if (!pluginInfo.isEnabled()) {
            tryAppendValue(argumentList, "sl.enabled", "false");
        }

        tryAppendValue(argumentList, "sl.enableUpgrade", "false");
        tryAppendValue(argumentList, "sl.customerId", pluginInfo.getCustomerId());
        tryAppendValue(argumentList, "sl.server", pluginInfo.getServerUrl());
        tryAppendValue(argumentList, "sl.proxy", pluginInfo.getProxy());

        String appName = pluginInfo.getAppName();
        if ("Build Per Module".equalsIgnoreCase(pluginInfo.getBuildStrategy().getDisplayName())) {
            appName = "[" + pluginInfo.getAppName() + "] - " + pluginInfo.getModuleName();
        }
        tryAppendValue(argumentList, "sl.appName", appName);

        tryAppendValue(argumentList, "sl.moduleName", pluginInfo.getModuleName());
        tryAppendValue(argumentList, "sl.buildName", pluginInfo.getBuildName());
        tryAppendValue(argumentList, "sl.branchName", pluginInfo.getBranchName());
        tryAppendValue(argumentList, "sl.includes", pluginInfo.getPackagesIncluded());

        String packagesExcluded = "*FastClassByGuice*, *ByCGLIB*, *EnhancerByMockitoWithCGLIB*, *EnhancerBySpringCGLIB*";
        if (!isNullOrEmpty(pluginInfo.getPackagesExcluded()))
            packagesExcluded = packagesExcluded + ", " + pluginInfo.getPackagesExcluded();
        tryAppendValue(argumentList, "sl.excludes", packagesExcluded);

        String classLoaderExcluded = "org.powermock.core.classloader.MockClassLoader";
        if (!isNullOrEmpty(pluginInfo.getClassLoadersExcluded()))
            classLoaderExcluded = classLoaderExcluded + ", " + pluginInfo.getClassLoadersExcluded();
        tryAppendValue(argumentList, "sl.classLoadersExcluded", classLoaderExcluded);

        tryAppendValue(argumentList, "sl.fileStorage", pluginInfo.getFilesStorage());
        tryAppendValue(argumentList, "sl.config.file", pluginInfo.getListenerConfigFile());
        tryAppendValue(argumentList, "sl.environmentName", pluginInfo.getEnvironment());

        tryAppendValue(argumentList, "sl.pathToMetaJson", pluginInfo.getFixedMetaJsonPath());

        if (pluginInfo.isLogEnabled()) {
            tryAppendValue(argumentList, "sl.log.enabled", "true");

            String logLevel = LogLevel.INFO.name();
            if (pluginInfo.getLogLevel() != null)
                logLevel = pluginInfo.getLogLevel().name();
            tryAppendValue(argumentList, "sl.log.level", logLevel);

            if (pluginInfo.getLogDestination() != null && "file".equalsIgnoreCase(pluginInfo.getLogDestination().name())) {
                tryAppendValue(argumentList, "sl.log.toFile", "true");
                tryAppendValue(argumentList, "sl.log.filename", "test-listener");
                tryAppendValue(argumentList, "sl.log.folder", detectLogFolder(pluginInfo.getFilesStorage(), pluginInfo.getLogFolder(), logFolderName));
            }
        }

        String listenerArgument = "-javaagent:"+pluginInfo.getFixedTestListenerPath();
        argumentList.add(listenerArgument);

        return argumentList;
    }

    /**
     *
     * @param filesStorage the customer files storage where sealights saves files
     * @param logFolderPath the path (absolute/relative) to the base folder where the logs will be saved
     * @param logFolderName the name of the folder at the end of the @logFolderPath where log files are saved
     * @return The absolute path to the folder where sealights should save the log files
     */
    private static String detectLogFolder(String filesStorage, String logFolderPath, String logFolderName){
        if (PathUtils.isAbsolutePath(logFolderPath)){
            logFolderPath = PathUtils.join(logFolderPath, logFolderName);
        }else if (!StringUtils.isNullOrEmpty(filesStorage)){
            logFolderPath = PathUtils.join(filesStorage, logFolderPath, logFolderName);
        }else{
            logFolderPath = PathUtils.join(System.getProperty("java.io.tmpdir"), logFolderPath, logFolderName);
        }
        return logFolderPath;
    }
    private static void tryAppendValue(List<String> argumentList, String property, String value) {
        if (!isNullOrEmpty(value)) {
            argumentList.add("-D"+property+"="+value);
        }
    }
}
