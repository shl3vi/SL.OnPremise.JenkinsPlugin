package io.sealights.plugins.sealightsjenkins.integration.plugins.external;

import io.sealights.plugins.sealightsjenkins.LogLevel;
import io.sealights.plugins.sealightsjenkins.integration.Commons;
import io.sealights.plugins.sealightsjenkins.integration.PomFile;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.integration.plugins.PluginIntegrator;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

import static io.sealights.plugins.sealightsjenkins.utils.StringUtils.isNullOrEmpty;

/**
 * This class help to add sealights arguments to com.lazerycode.jmeter:jmeter-maven-plugin plugin in a pom file.
 */
public class LazerycodeJMeterPluginIntegrator extends PluginIntegrator {

    private SeaLightsPluginInfo pluginInfo;
    private Document pomDoc;

    public LazerycodeJMeterPluginIntegrator(Logger logger, SeaLightsPluginInfo pluginInfo, PomFile pomFile) {
        super(logger, pomFile);
        this.pluginInfo = pluginInfo;
        this.pomDoc = pomFile.getDocument();
    }

    /**
     * @return 'argument' elements list.
     *
     * This function create an XML similar to the following:
     * <argument>-Dsl.enableUpgrade=false</argument>
     * <argument>-Dsl.customerId=fake-customer-id-123</argument>
     * <argument>-Dsl.server=http://fake-server-url.com</argument>
     * <argument>-Dsl.appName=fake-app-name</argument>
     * <argument>-Dsl.moduleName=fake-module-name</argument>
     * <argument>-Dsl.buildName=1</argument>
     * <argument>-Dsl.branchName=fake-branch</argument>
     * <argument>-Dsl.includes=com.fake.*</argument>
     * <argument>-Dsl.excludes=*FastClassByGuice*, *ByCGLIB*, *EnhancerByMockitoWithCGLIB*, *EnhancerBySpringCGLIB*, com.fake.excluded.*</argument>
     * <argument>-Dsl.classLoadersExcluded=org.powermock.core.classloader.MockClassLoader</argument>
     * <argument>-javaagent:/path/to/override-sl-test-listener.jar</argument>
     *
     * @throws Exception
     */
    private List<Element> createArgumentElementList() throws Exception {

        List<Element> argumentElementList = new ArrayList<>();

        List<String> slArgumentList = createSLArgumentList(pluginInfo, "jMeter");
        for (String arg : slArgumentList) {
            String xmlArg = "<argument>" + arg + "</argument>";
            Element argElement = pomFile.createElement(xmlArg);
            if (argElement == null) {
                logger.warning("Unable to add argument element '" + xmlArg + "' to '" + pluginDescriptor() + "' plugin.");
                continue;
            }
            argumentElementList.add(argElement);
        }

        return argumentElementList;
    }

    @Override
    protected String artifactId() {
        return "jmeter-maven-plugin";
    }

    @Override
    protected String groupId() {
        return "com.lazerycode.jmeter";
    }

    private final boolean INCLUDE_ALL_DESCENDANTS = true;

    @Override
    protected void integrate() throws Exception {

        List<Element> jMeterPlugins = pomFile.getPluginsOccurrencesInParent(artifactId(), pomDoc.getDocumentElement(), INCLUDE_ALL_DESCENDANTS);
        for (Element jMeterPlugin : jMeterPlugins) {
            Element arguments = getOrCreateArgumentsElement(jMeterPlugin);
            if (arguments == null) {
                logger.warning("Unable to find/create 'jMeterProcessJVMSettings/arguments' element in '" + jMeterPlugin.getBaseURI() + "'");
                continue;
            }
            List<Element> slArgumentList = createArgumentElementList();
            for (Element slArgument : slArgumentList) {
                arguments.appendChild(slArgument);
            }
            logger.debug("Integrated to plugin '" + pluginDescriptor() + "'.");
        }
    }

    private Element getOrCreateArgumentsElement(Element jMeterPlugin) {
        try {
            List<Element> executionsElementList = pomFile.getOrCreateElements("executions", jMeterPlugin);
            List<Element> executionElementList = pomFile.getOrCreateElements("execution", executionsElementList.get(0));
            List<Element> configurationElementList = pomFile.getOrCreateElements("configuration", executionElementList.get(0));
            List<Element> jMeterProcessJVMSettings = pomFile.getOrCreateElements("jMeterProcessJVMSettings", configurationElementList.get(0));
            List<Element> arguments = pomFile.getOrCreateElements("arguments", jMeterProcessJVMSettings.get(0));
            if (!arguments.isEmpty())
                return arguments.get(0);
        } catch (Exception e) {
            logger.error("Unable to find or create 'arguments' tag in jMeter plugin. Error:", e);
        }

        return null;
    }

    private boolean isSealightsAlreadyConfigured(Element argumentsElement) throws XPathExpressionException {
        List<Element> argumentElementList = pomFile.getElements("argument", argumentsElement);
        for (Element e : argumentElementList) {
            String arg = e.getTextContent();
            if (arg.contains("-Dsl.customerId") || arg.contains("-Dsl.token")) {
                logger.info("Found sealights argument '" + arg + "' in '" + pluginDescriptor() + "' plugin. " +
                        "Assuming sealights is already configured. " +
                        "Skipping this plugin integration.");
                return true;
            }
        }
        return false;
    }

    public static List<String> createSLArgumentList(SeaLightsPluginInfo pluginInfo, String logFolderName)
            throws Exception {

        List<String> argumentList = new ArrayList<>();

        if (!pluginInfo.isEnabled()) {
            tryAppendValue(argumentList, Commons.ENABLED_PROPERTY, "false");
        }

        tryAppendValue(argumentList, Commons.ENABLE_UPGRADE_PROPERTY, "false");

        if (pluginInfo.getTokenData() != null){
            tryAppendValue(argumentList, Commons.TOKEN_PROPERTY, pluginInfo.getTokenData().getToken());
        }else{
            tryAppendValue(argumentList, Commons.CUSTOMER_ID_PROPERTY, pluginInfo.getCustomerId());
            tryAppendValue(argumentList, Commons.SERVER_PROPERTY, pluginInfo.getServerUrl());
        }


        tryAppendValue(argumentList, Commons.PROXY_PROPERTY, pluginInfo.getProxy());

        String appName = pluginInfo.getAppName();
        if ("Build Per Module".equalsIgnoreCase(pluginInfo.getBuildStrategy().getDisplayName())) {
            appName = "[" + pluginInfo.getAppName() + "] - " + pluginInfo.getModuleName();
        }
        tryAppendValue(argumentList, Commons.APP_NAME_PROPERTY, appName);

        tryAppendValue(argumentList, Commons.MODULE_NAME_PROPERTY, pluginInfo.getModuleName());
        tryAppendValue(argumentList, Commons.BUILD_NAME_PROPERTY, pluginInfo.getBuildName());
        tryAppendValue(argumentList, Commons.BRANCH_NAME_PROPERTY, pluginInfo.getBranchName());
        tryAppendValue(argumentList, Commons.INCLUDES_PROPERTY, pluginInfo.getPackagesIncluded());

        String packagesExcluded = "*FastClassByGuice*, *ByCGLIB*, *EnhancerByMockitoWithCGLIB*, *EnhancerBySpringCGLIB*";
        if (!isNullOrEmpty(pluginInfo.getPackagesExcluded()))
            packagesExcluded = packagesExcluded + ", " + pluginInfo.getPackagesExcluded();
        tryAppendValue(argumentList, Commons.EXCLUDES_PROPERTY, packagesExcluded);

        String classLoaderExcluded = "org.powermock.core.classloader.MockClassLoader";
        if (!isNullOrEmpty(pluginInfo.getClassLoadersExcluded()))
            classLoaderExcluded = classLoaderExcluded + ", " + pluginInfo.getClassLoadersExcluded();
        tryAppendValue(argumentList, Commons.CLASS_LOADERS_EXCLUDED_PROPERTY, classLoaderExcluded);

        tryAppendValue(argumentList, Commons.FILES_STORAGE_PROPERTY, pluginInfo.getFilesStorage());
        tryAppendValue(argumentList, Commons.CONFIG_FILE_PROPERTY, pluginInfo.getListenerConfigFile());
        tryAppendValue(argumentList, Commons.ENVIRONMENT_NAME_PROPERTY, pluginInfo.getEnvironment());

        tryAppendValue(argumentList, Commons.PATH_TO_META_JSON_PROPERTY, pluginInfo.getOverrideMetaJsonPath());

        if (pluginInfo.isLogEnabled()) {
            tryAppendValue(argumentList, Commons.LOG_ENABLED_PROPERTY, "true");

            String logLevel = LogLevel.INFO.name();
            if (pluginInfo.getLogLevel() != null)
                logLevel = pluginInfo.getLogLevel().name();
            tryAppendValue(argumentList, Commons.LOG_LEVEL_PROPERTY, logLevel);

            if (pluginInfo.getLogDestination() != null && "file".equalsIgnoreCase(pluginInfo.getLogDestination().name())) {
                tryAppendValue(argumentList, Commons.LOG_TO_FILE_PROPERTY, "true");
                tryAppendValue(argumentList, Commons.LOG_FILE_NAME_PROPERTY, "test-listener");
                tryAppendValue(argumentList, Commons.LOG_FOLDER_PROPERTY, pluginInfo.getLogFolder());
            }
        }

        String fixedListenerPath = pluginInfo.getOverrideTestListenerPath();
        if (StringUtils.isNullOrEmpty(fixedListenerPath)) {
            throw new Exception("Unable to add argument '-javaagent' to the plugin. Missing path to the java agent.");
        }
        String listenerArgument = "-javaagent:" + fixedListenerPath;
        argumentList.add(listenerArgument);

        return argumentList;
    }

    private static void tryAppendValue(List<String> argumentList, String property, String value) {
        if (!isNullOrEmpty(value)) {
            argumentList.add("-D" + property + "=" + value);
        }
    }

    @Override
    public boolean isAlreadyIntegrated() {
        try {
            List<Element> jMeterPlugins = pomFile.getPluginsOccurrencesInParent(artifactId(), pomDoc.getDocumentElement(), INCLUDE_ALL_DESCENDANTS);
            for (Element jMeterPlugin : jMeterPlugins) {
                Element arguments = getOrCreateArgumentsElement(jMeterPlugin);
                boolean isSealightsConfigured = isSealightsAlreadyConfigured(arguments);
                if (isSealightsConfigured)
                    return true;
            }

        } catch (Exception e) {
            logger.error("Unable to check if the pom was integrated manually with Sealights. " +
                    "Assuming that Sealights is already integrated. " +
                    "Error: ", e);
            return true;
        }

        return false;
    }

}
