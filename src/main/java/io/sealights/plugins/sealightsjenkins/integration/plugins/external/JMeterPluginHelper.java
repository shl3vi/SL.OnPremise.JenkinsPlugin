package io.sealights.plugins.sealightsjenkins.integration.plugins.external;

import io.sealights.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealights.plugins.sealightsjenkins.integration.PomXmlUtils;
import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.integration.plugins.PluginIntegrationHelper;
import io.sealights.plugins.sealightsjenkins.integration.plugins.PluginsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class help to integrate with the com.lazerycode.jmeter:jmeter-maven-plugin plugin.
 */
public class JMeterPluginHelper extends PluginIntegrationHelper {

    private SeaLightsPluginInfo pluginInfo;
    private Document pomDoc;
    private Logger logger;

    public JMeterPluginHelper(Logger logger, MavenIntegrationInfo mavenIntegrationInfo, Document pomDoc) {
        this.pluginInfo = mavenIntegrationInfo.getSeaLightsPluginInfo();
        this.pomDoc = pomDoc;
        this.logger = logger;
    }

    private List<Element> createArgumentElementList() throws IOException, SAXException, ParserConfigurationException {

        List<Element> argumentElementList = new ArrayList<>();

        List<String> slArgumentList = PluginsUtils.createSLArgumentList(pluginInfo, "jMeter");
        for (String arg : slArgumentList){
            try {
                String xmlArg = "<argument>" + arg + "</argument>";
                Element e = PomXmlUtils.createElement(xmlArg, pomDoc);
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
            List<Element> jMeterPlugins = PomXmlUtils.getPluginsOccurrencesInParent(artifactId(), pomDoc.getDocumentElement(), INCLUDE_ALL_DESCENDANTS);
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

    private Element getArgumentsElement(Element jMeterPlugin){
        try {
            List<Element> executionsElementList = PomXmlUtils.getOrCreateElements("executions", jMeterPlugin, pomDoc);
            List<Element> executionElementList = PomXmlUtils.getOrCreateElements("execution", executionsElementList.get(0), pomDoc);
            List<Element> configurationElementList = PomXmlUtils.getOrCreateElements("configuration", executionElementList.get(0), pomDoc);
            List<Element> jMeterProcessJVMSettings = PomXmlUtils.getOrCreateElements("jMeterProcessJVMSettings", configurationElementList.get(0), pomDoc);
            List<Element> arguments = PomXmlUtils.getOrCreateElements("arguments", jMeterProcessJVMSettings.get(0), pomDoc);
            if (!arguments.isEmpty())
                return arguments.get(0);
        }catch (Exception e){
            logger.error("Unable to find or create 'arguments' tag in jMeter plugin. Error:", e);
        }

        return null;
    }

    private boolean isSealightsAlreadyConfigured(Element argumentsElement) throws XPathExpressionException {
        List<Element> argumentElementList = PomXmlUtils.getElements("argument", argumentsElement);
        for (Element e : argumentElementList) {
            String arg = e.getTextContent();
            if (arg.contains("-Dsl."))
                return true;
        }
        return false;
    }

}
