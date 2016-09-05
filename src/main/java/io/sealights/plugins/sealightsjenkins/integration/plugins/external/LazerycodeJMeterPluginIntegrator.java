package io.sealights.plugins.sealightsjenkins.integration.plugins.external;

import io.sealights.plugins.sealightsjenkins.LogLevel;
import io.sealights.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealights.plugins.sealightsjenkins.integration.PomFile;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.integration.plugins.PluginIntegrator;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.PathUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

import static io.sealights.plugins.sealightsjenkins.utils.StringUtils.isNullOrEmpty;

/**
 * This class help to add sealights arguments to com.lazerycode.jmeter:jmeter-maven-plugin plugin in a pom file.
 */
public class LazerycodeJMeterPluginIntegrator extends PluginIntegrator {

    private SeaLightsPluginInfo pluginInfo;
    private PomFile pomFile;
    private Document pomDoc;
    private Logger logger;

    public LazerycodeJMeterPluginIntegrator(Logger logger, MavenIntegrationInfo mavenIntegrationInfo, PomFile pomFile) {
        this.pluginInfo = mavenIntegrationInfo.getSeaLightsPluginInfo();
        this.pomFile = pomFile;
        this.pomDoc = pomFile.getDocument();
        this.logger = logger;
    }

    private List<Element> createArgumentElementList() throws Exception {

        List<Element> argumentElementList = new ArrayList<>();

        List<String> slArgumentList = createSLArgumentList(pluginInfo, "jMeter");
        for (String arg : slArgumentList){
            try {
                String xmlArg = "<argument>" + arg + "</argument>";
                Element e = pomFile.createElement(xmlArg);
                argumentElementList.add(e);
            }catch (SAXParseException e){
                logger.error("Unable to parse string '<argument>"+arg+"</argument>' to xml element. Reason: "+e.getMessage());
            }catch (Exception e){
                logger.error("Can't add argument '"+arg+"' to the jMeter plugin. Error:", e);
            }
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
    public void integrate() {
        try {
            if (shouldSkipIntegration()){
                logger.info("'"+skipPropertyName()+"' property is set to true. skipping this plugin integration.");
                return;
            }

            List<Element> jMeterPlugins = pomFile.getPluginsOccurrencesInParent(artifactId(), pomDoc.getDocumentElement(), INCLUDE_ALL_DESCENDANTS);
            for (Element jMeterPlugin : jMeterPlugins){
                Element arguments = getArgumentsElement(jMeterPlugin);
                if (arguments == null)
                    continue;
                boolean isSealightsConfigured = isSealightsAlreadyConfigured(arguments);
                if (isSealightsConfigured)
                    continue;
                List<Element> slArgumentList = createArgumentElementList();
                for (Element slArgument : slArgumentList){
                    arguments.appendChild(slArgument);
                }
                logger.debug("Integrated to plugin '"+pluginDescriptor()+"'.");
            }
        }catch (Exception e){
            logger.error("Unable to integrate to plugin '"+pluginDescriptor()+"'. Error:", e);
        }
    }

    private boolean shouldSkipIntegration() throws XPathExpressionException {
        String skipPropertyName = skipPropertyName();
        List<Element> propertyElementList = pomFile.getProperties();

        for (Element propertyElement : propertyElementList){
            if (!skipPropertyName.equals(propertyElement.getTagName()))
                continue;
            if ("true".equalsIgnoreCase(propertyElement.getTextContent()))
                return true;
        }

        return false;
    }

    private Element getArgumentsElement(Element jMeterPlugin){
        try {
            List<Element> executionsElementList = pomFile.getOrCreateElements("executions", jMeterPlugin);
            List<Element> executionElementList = pomFile.getOrCreateElements("execution", executionsElementList.get(0));
            List<Element> configurationElementList = pomFile.getOrCreateElements("configuration", executionElementList.get(0));
            List<Element> jMeterProcessJVMSettings = pomFile.getOrCreateElements("jMeterProcessJVMSettings", configurationElementList.get(0));
            List<Element> arguments = pomFile.getOrCreateElements("arguments", jMeterProcessJVMSettings.get(0));
            if (!arguments.isEmpty())
                return arguments.get(0);
        }catch (Exception e){
            logger.error("Unable to find or create 'arguments' tag in jMeter plugin. Error:", e);
        }

        return null;
    }

    private boolean isSealightsAlreadyConfigured(Element argumentsElement) throws XPathExpressionException {
        List<Element> argumentElementList = pomFile.getElements("argument", argumentsElement);
        for (Element e : argumentElementList) {
            String arg = e.getTextContent();
            if (arg.contains("-Dsl.")) {
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

        String fixedListenerPath = pluginInfo.getFixedTestListenerPath();
        if (StringUtils.isNullOrEmpty(fixedListenerPath)){
            throw new Exception("Unable to add argument '-javaagent' to the plugin. Missing path to the java agent.");
        }
        String listenerArgument = "-javaagent:"+fixedListenerPath;
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
