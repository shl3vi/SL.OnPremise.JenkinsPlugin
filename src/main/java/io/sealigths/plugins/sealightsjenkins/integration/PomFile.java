package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.utils.Logger;
import io.sealigths.plugins.sealightsjenkins.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadav on 4/18/2016.
 */
public class PomFile {

    protected Logger log;
    protected String filename;
    private Document document;
    private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private final static String SUREFIRE_XML = "<groupId>org.apache.maven.plugins</groupId><artifactId>maven-surefire-plugin</artifactId><version>2.19</version>";

    public PomFile(String filename, Logger log) {
        this.filename = filename;
        this.log = log;
    }


    public boolean isPluginExistInEntirePom(String artifactId) {
        try {
            return isPluginExistInElement(artifactId, getDocument().getDocumentElement(), true);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addPlugin(String pluginBodyAsXml) {
        Document doc = this.getDocument();
        try {
            Element docElement = doc.getDocumentElement();
            addPlugin(pluginBodyAsXml, docElement);
            addPluginToAllProfiles(pluginBodyAsXml, docElement);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private void addPluginToAllProfiles(String pluginBodyAsXml, Element parent) throws XPathExpressionException {
        List<Element> profilesList = getElements("profiles", parent);
        for (Element profiles : profilesList){
            List<Element> profileList = getElements("profile", profiles);
            for(Element profile : profileList){
                addPlugin(pluginBodyAsXml, profile);
            }
        }
    }

    public void addPlugin(String pluginBodyAsXml, Element parentElement) throws XPathExpressionException {
        List<Element> buildElements = getOrCreateElements("build", parentElement);

        for (Element buildElement : buildElements) {
            verifyPluginsElement(pluginBodyAsXml, buildElement);

            if (isNodeExist(buildElement, "pluginManagement")) {
                List<Element> pluginManagementElements = getOrCreateElements("pluginManagement", buildElement);
                verifyPluginsElement(pluginBodyAsXml, pluginManagementElements.get(0));
            }
        }
    }

    private void verifyPluginsElement(String pluginBodyAsXml, Element parentElement) throws XPathExpressionException {
        List<Element> pluginsElements = getOrCreateElements("plugins", parentElement);
        try {

            for (int i = 0; i < pluginsElements.size(); i++) {
                Element pluginsElement = pluginsElements.get(i);
                if (!isPluginExistInElement(SUREFIRE_ARTIFACT_ID, pluginsElement)) {
                    //Surefire doesn't exist in element. it it.
                    addPluginToPluginsElement(SUREFIRE_XML, pluginsElement);
                }

                addPluginToPluginsElement(pluginBodyAsXml, pluginsElement);
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void addPluginToPluginsElement(String pluginBodyAsXml, Element pluginsElement) throws SAXException, IOException, ParserConfigurationException {
        String xml = "<plugin>" + pluginBodyAsXml + "</plugin>";
        Element pluginElement = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))))
                .getDocumentElement();

        pluginElement = (Element) document.importNode(pluginElement, true);
        pluginsElement.appendChild(pluginElement);
    }

    String SUREFIRE_PLUGIN = "//plugin/artifactId[.='maven-surefire-plugin']/parent::plugin";
    String SUREFIRE_PLUGIN_IN_PLUGIN_MGMT = "//pluginManagement/plugins/plugin/artifactId[.='maven-surefire-plugin']/parent::plugin";

    public void updateSurefirePlugin(String listenerValue, String apiJarPath) {
        Element documentElement = getDocument().getDocumentElement();
        updateSurefirePlugin(documentElement, listenerValue, apiJarPath);
    }

    private void updateSurefirePlugin(Element parentElement, String listenerValue, String apiJarPath) {
        try {


            List<Element> surefireElements = getOrCreateElements("plugin", SUREFIRE_PLUGIN, parentElement);
            for (Element surefireElement : surefireElements) {
                List<Element> configurationElements = getOrCreateElements("configuration", surefireElement);
                for (Element configurationElement : configurationElements) {
                    if (isNodeExist(configurationElement, "forkMode")) {
                        if (!isValidForkMode(configurationElement)) {
                            log.warning("Skipping SeaLights integration due to unsupported 'forkMode' value of SureFire. Value cannot be 'never' or 'always'. Recommended value is 'once'.");
                            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Skipping SeaLights integration due to unsupported 'forkMode' value of SureFire. Value cannot be 'never' or 'always'. Recommended value is 'once'.");
                            continue;
                        }
                    }

                    if (isNodeExist(configurationElement, "forkCount")) {
                        if (!isValidForkCount(configurationElement)) {
                            log.warning("Skipping SeaLights integration due to unsupported 'forkCount' value of SureFire. Value cannot be '0'.");
                            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Skipping SeaLights integration due to unsupported 'forkCount' value of SureFire. Value cannot be '0'.");
                            continue;
                        }
                    }


                    if (!StringUtils.isNullOrEmpty(listenerValue))
                        verifyPropertiesElement(listenerValue, configurationElement);
                    verifyAdditionalClasspathElements(apiJarPath, configurationElement);
                    verifyArgLineElement(configurationElement);
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidPom() {

        Element documentElement = getDocument().getDocumentElement();
        if (documentElement == null) {
            log.warning("Couldn't read pom file (documentElement is null).");
            return false;
        }

        try {
            if (!isNodeExist(documentElement, SUREFIRE_PLUGIN_IN_PLUGIN_MGMT))
                return true;
            List<Element> surefireElements = getOrCreateElements("plugin", SUREFIRE_PLUGIN_IN_PLUGIN_MGMT, documentElement);
            for (Element surefireElement : surefireElements) {
                List<Element> configurationElements = getOrCreateElements("configuration", surefireElement);
                for (Element configurationElement : configurationElements) {
                    if (isNodeExist(configurationElement, "forkMode")) {
                        if (!isValidForkMode(configurationElement)) {
                            log.warning("Found an unsupported 'forkMode' value of SureFire. Value cannot be 'never' or 'always'. Recommended value is 'once'.");
                            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Found an unsupported 'forkMode' value of SureFire. Value cannot be 'never' or 'always'. Recommended value is 'once'.");
                        }
                    }

                    if (isNodeExist(configurationElement, "forkCount")) {
                        if (!isValidForkCount(configurationElement)) {
                            log.warning("Found an unsupported 'forkCount' value of SureFire. Value cannot be '0'.");
                            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Found an unsupported 'forkCount' value of SureFire. Value cannot be '0'.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed while trying to validate the pom. Error:", e);
            return false;
        }

        return true;
    }

    private boolean isValidForkMode(Element configurationElement) throws XPathExpressionException {
        List<Element> forkModeElements = getOrCreateElements("forkMode", configurationElement);
        Element forkMode = forkModeElements.get(0);
        String currentValue = forkMode.getTextContent();
        boolean isValid = !("never".equalsIgnoreCase(currentValue) || "always".equalsIgnoreCase(currentValue));
        return isValid;
    }

    private boolean isValidForkCount(Element configurationElement) throws XPathExpressionException {
        List<Element> forkCountElements = getOrCreateElements("forkCount", configurationElement);
        Element forkCount = forkCountElements.get(0);
        String currentValue = forkCount.getTextContent();
        boolean isValid = !("0".equalsIgnoreCase(currentValue));
        return isValid;
    }

    private void verifyArgLineElement(Element configurationElement) throws XPathExpressionException {
        if (isNodeExist(configurationElement, "./argLine")) {
            //We have argLine node. If that's the case, we must make sure that it contains ${argLine}
            // or else it will not invoke our Test Listener and customers will get ClassNotFoundException on our classes.
            List<Element> argLineElements = getOrCreateElements("argLine", configurationElement);
            Element argLine = argLineElements.get(0);
            String currentValue = argLine.getTextContent();
            if (!currentValue.contains("${argLine}")) {
                currentValue = "${argLine} " + currentValue;
                argLine.setTextContent(currentValue);
            }
        }
    }

    private void verifyPropertiesElement(String listenerValue, Element configurationElement) throws XPathExpressionException {
        List<Element> propertiesElements = getOrCreateElements("properties", configurationElement);
        for (Element propertiesElement : propertiesElements) {
            List<Element> propertyElements = getElements("property", propertiesElement);
            boolean foundListenerProperty = isFoundListenerProperty(listenerValue, propertyElements);

            if (!foundListenerProperty) {
                //Add one.
                Element propertyElement = getDocument().createElement("property");
                Element name = getDocument().createElement("name");
                name.setTextContent("listener");

                Element value = getDocument().createElement("value");
                value.setTextContent(listenerValue);

                propertyElement.appendChild(name);
                propertyElement.appendChild(value);

                propertiesElement.appendChild(propertyElement);
            }
        }
    }

    private void verifyAdditionalClasspathElements(String apiJarPath, Element configurationElement) throws XPathExpressionException {
        List<Element> additionalClasspathElementsList = getOrCreateElements("additionalClasspathElements", configurationElement);

        for (Element additionalClasspathElements : additionalClasspathElementsList) {
            List<Element> additionalClasspathElementList = getElements("additionalClasspathElement", additionalClasspathElements);

            boolean foundApiJar = isAdditonalClasspathElementWithApiJar(additionalClasspathElementList);
            if (foundApiJar)
                return;

            //Not found. Add one.
            Element classPathElement = createElement("additionalClasspathElement", apiJarPath);
            additionalClasspathElements.appendChild(classPathElement);
        }
    }

    private Element createElement(String tagName, String textContent) {
        Document doc = this.getDocument();
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        return element;
    }

    private boolean isFoundListenerProperty(String listenerValue, List<Element> propertiesElements) throws XPathExpressionException {
        for (Element propertyElement : propertiesElements) {
            {
                //Does the current property is a listener property.
                if (!isNodeExist(propertyElement, "name"))
                    continue;

                List<Element> nameElements = getElements("name", propertyElement);
                Element name = nameElements.get(0);
                if (!"listener".equalsIgnoreCase(name.getTextContent()))
                    continue;

                if (isNodeExist(propertyElement, "value")) {
                    //Update the current value
                    List<Element> valueElements = getOrCreateElements("value", propertyElement);
                    Element valueElement = valueElements.get(0);
                    String currentValue = valueElements.get(0).getTextContent();
                    if (!StringUtils.isNullOrEmpty(currentValue) && !currentValue.contains(listenerValue)) {
                        currentValue += ", " + listenerValue;
                    } else {
                        currentValue = listenerValue;
                    }
                    valueElement.setTextContent(currentValue);

                } else {
                    //A "value" element doesn't exist. Add it.
                    Element valueElement = createElement("value", listenerValue);
                    propertyElement.appendChild(valueElement);
                }

                return true;
            }
        }
        return false;
    }

    private boolean isAdditonalClasspathElementWithApiJar(List<Element> additionalClasspathElementList) throws XPathExpressionException {
        for (Element additionalClasspathElement : additionalClasspathElementList) {
            String elementTextContent = additionalClasspathElement.getTextContent();
            if (elementTextContent.contains("sl-api") && elementTextContent.endsWith(".jar")) {
                log.debug("'additionalClasspathElement' node with path to '*sl-api*.jar' already exist. No need to add it.");
                return true;
            }
        }
        return false;
    }


    public void save(String filename) throws TransformerException, IOException, InterruptedException {
        // write the DOM object to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(getDocument());

        saveInternal(filename, transformer, domSource);
    }

    protected void saveInternal(String filename, Transformer transformer, DOMSource domSource) throws TransformerException, IOException, InterruptedException {
        StreamResult streamResult = new StreamResult(new File(filename));
        transformer.transform(domSource, streamResult);
    }

    private List<Element> getOrCreateElements(String nameAndXpath, Element parent) throws XPathExpressionException {
        return getOrCreateElements(nameAndXpath, nameAndXpath, parent);
    }

    private List<Element> getOrCreateElements(String name, String xpath, Element parent) throws XPathExpressionException {

        List<Element> childElements = getElements(xpath, parent);

        if (childElements.isEmpty()) {
            Document doc = this.getDocument();
            Element child = doc.createElement(name);
            parent.appendChild(child);
            childElements.add(child);
        }

        return childElements;
    }

    private List<Element> getElements(String xpath, Element parent) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xpath);
        NodeList nodes = (NodeList) expression.evaluate(parent, XPathConstants.NODESET);
        List<Element> childElements = new ArrayList<Element>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                childElements.add((Element) node);
        }

        return childElements;
    }

    String PLUGIN_TEMPLATE = "plugin[artifactId='#ARTIFACT_ID#']";

    private boolean isPluginExistInElement(String artifactId, Element parent) throws XPathExpressionException {
        return isPluginExistInElement(artifactId, parent, false);
    }

    private boolean isPluginExistInElement(String artifactId, Element parent, boolean includeAllDescendants) throws XPathExpressionException {
        String xpath = PLUGIN_TEMPLATE;
        if (includeAllDescendants)
            xpath = "//" + xpath;

        xpath = xpath.replace("#ARTIFACT_ID#", artifactId);
        return isNodeExist(parent, xpath);
    }


    private boolean isNodeExist(Element parent, String xpathToNode) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xpathToNode);
        NodeList nodes = (NodeList) expression.evaluate(parent, XPathConstants.NODESET);
        return nodes.getLength() > 0;
    }

    private Document getDocument() {
        if (document != null)
            return document;
        try {

            document = getDocumentInternal();
            document.getDocumentElement().normalize();

        } catch (SAXException | ParserConfigurationException | InterruptedException | IOException e) {
            document = new EmptyDocument();
            log.error("Failed to get XML document. Error:", e);
        }
        return document;
    }


    protected Document getDocumentInternal() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(this.filename);
        return doc;
    }

}
