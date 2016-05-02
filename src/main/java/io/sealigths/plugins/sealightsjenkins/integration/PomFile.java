package io.sealigths.plugins.sealightsjenkins.integration;

import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadav on 4/18/2016.
 */
public class PomFile {

    private String filename;
    private Document document;
    private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";
    private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private final static String SUREFIRE_XML = "<groupId>org.apache.maven.plugins</groupId><artifactId>maven-surefire-plugin</artifactId><version>2.19</version>";

    public PomFile(String filename) {
        this.filename = filename;
    }


    public List<String> getProfileIds() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = null;
        try {
            XPathExpression expression = xPath.compile("//profiles/profile/id");
            nodes = (NodeList) expression.evaluate(getDocument(), XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }


        List<String> profiles = new ArrayList<>();

        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                profiles.add(node.getTextContent());
            }
        }

        return profiles;
    }

    public boolean isPluginExistInEntriePom(String groupId, String artifactId) {
        try {
            return isPluginExistInElement(groupId, artifactId, getDocument().getDocumentElement());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addPlugin(String pluginBodyAsXml) {
        Document doc = this.getDocument();
        try {
            addPlugin(pluginBodyAsXml, doc.getDocumentElement());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public void addPlugin(String pluginBodyAsXml, Element parentElement) throws XPathExpressionException {
        List<Element> buildElements = getOrCreateElements("build", "//build", parentElement);

        for (Element buildElement : buildElements) {
            verifyPluginsElement(pluginBodyAsXml, buildElement);
            if (isNodeExist(buildElement, "./pluginManagement")) {
                List<Element> pluginManagementElements = getOrCreateElements("pluginManagement", "pluginManagement", buildElement);
                verifyPluginsElement(pluginBodyAsXml, pluginManagementElements.get(0));
            }
        }
    }

    private void verifyPluginsElement(String pluginBodyAsXml, Element parentElement) throws XPathExpressionException {
        List<Element> pluginsElements = getOrCreateElements("plugins", "plugins", parentElement);
        try {

            for (int i = 0; i < pluginsElements.size(); i++) {
                Element pluginsElement = pluginsElements.get(i);

                if (!isPluginExistInElement(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID, pluginsElement)) {
                    //Surefire doesn't exist in element. it it.
                    addPluginToPluginsElement(SUREFIRE_XML, pluginsElement);
                }

                addPluginToPluginsElement(pluginBodyAsXml, pluginsElement);
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
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

    //String SUREFIRE_PLUGIN = "//build/plugins/plugin/artifactId[.='maven-surefire-plugin']/parent::plugin";
    String SUREFIRE_PLUGIN = "//plugin/artifactId[.='maven-surefire-plugin']/parent::plugin";

    public void updateSurefirePlugin(String listenerValue, String apiJarPath) {
        Element documentElement = getDocument().getDocumentElement();
        updateSurefirePlugin(documentElement, listenerValue, apiJarPath);
    }

    private void updateSurefirePlugin(Element parentElement, String listenerValue, String apiJarPath) {
        try {
            List<Element> surefireElements = getOrCreateElements("plugin", SUREFIRE_PLUGIN, parentElement);
            for (Element surefireElement : surefireElements) {
                List<Element> configurationElements = getOrCreateElements("configuration", "configuration", surefireElement);
                for (Element configurationElement : configurationElements) {
                    verifyPropertiesElement(listenerValue, configurationElement);
                    verifyAdditionalClasspathElements(apiJarPath, configurationElement);
                    verifyArgLineElement(configurationElement);
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private void verifyArgLineElement(Element configurationElement) throws XPathExpressionException {
        if (isNodeExist(configurationElement, "./argLine")) {
            //We have argLine node. If that's the case, we must make sure that it contains ${argLine}
            // or else it will not invoke our Test Listener and customers will get ClassNotFoundException on our classes.
            List<Element> argLineElements = getOrCreateElements("argLine", "argLine", configurationElement);
            Element argLine = argLineElements.get(0);
            String currentValue = argLine.getTextContent();
            if (!currentValue.contains("${argLine}"))
            {
                currentValue = "${argLine} " + currentValue;
                argLine.setTextContent(currentValue);
            }

            //combine.children="append"
            String combineChildren = argLine.getAttribute("combine.children");
            if (!"append".equalsIgnoreCase(combineChildren)){
                combineChildren = "append";
                argLine.setAttribute("combine.children", combineChildren);
            }

        }
    }

    private void verifyPropertiesElement(String listenerValue, Element configurationElement) throws XPathExpressionException {
        List<Element> propertiesElements = getOrCreateElements("properties", "properties", configurationElement);
        boolean foundListenerProperty = false;
        for (Element propertiesElement : propertiesElements) {
            List<Element> propertyElements = getOrCreateElements("property", "property", propertiesElement);
            foundListenerProperty = isFoundListenerProperty(listenerValue, propertiesElements, foundListenerProperty);

            if (!foundListenerProperty) {
                //Add one.
                Element name = getDocument().createElement("name");
                name.setTextContent("listener");

                Element value = getDocument().createElement("value");
                value.setTextContent(listenerValue);

                Element property = propertyElements.get(0);
                property.appendChild(name);
                property.appendChild(value);

            }
        }
    }

    private void verifyAdditionalClasspathElements(String apiJarPath, Element configurationElement) throws XPathExpressionException {
        List<Element> additionalClasspathElements = getOrCreateElements("additionalClasspathElements", "additionalClasspathElements", configurationElement);
        boolean foundApiJar = false;
        for (Element additionalClasspathElement : additionalClasspathElements) {
            List<Element> additionalClasspathElementList = getOrCreateElements("additionalClasspathElement", "additionalClasspathElement", additionalClasspathElement);
            foundApiJar = isFoundAdditonalClasspathElementWithApiJar(apiJarPath, additionalClasspathElements, foundApiJar);

            if (!foundApiJar) {
                //Add one.
                Element classPathElement = additionalClasspathElementList.get(0);
                classPathElement.setTextContent(apiJarPath);
            }
        }
    }

    private boolean isFoundListenerProperty(String listenerValue, List<Element> propertiesElements, boolean foundListenerProperty) throws XPathExpressionException {
        for (Element propertyElement : propertiesElements) {
            {
                //Does the current property is a listener property.
                if (isNodeExist(propertyElement, "./name[text() = 'listener']")) {
                    if (isNodeExist(propertyElement, "./value")) {
                        //Update the current value
                        List<Element> valueElements = getOrCreateElements("value", "./value", propertyElement);
                        Element valueElement = valueElements.get(0);
                        String currentValue = valueElements.get(0).getTextContent();
                        if (currentValue != null && !currentValue.equals("")) {
                            currentValue += ", " + listenerValue;
                        } else {
                            currentValue = listenerValue;
                        }

                        valueElement.setTextContent(currentValue);
                    } else {
                        //A "value" element doesn't exist. Add it.
                        List<Element> valueElements = getOrCreateElements("value", "./value", propertyElement);
                        valueElements.get(0).setTextContent(listenerValue);

                    }

                    foundListenerProperty = true;
                }


            }
        }
        return foundListenerProperty;
    }

    private boolean isFoundAdditonalClasspathElementWithApiJar(String apiJarPath, List<Element> propertiesElements, boolean foundListenerProperty) throws XPathExpressionException {
        for (Element propertyElement : propertiesElements) {
            {
                //Does the current property is a listener property.
                if (isNodeExist(propertyElement, "/additionalClasspathElement[text() = '"+apiJarPath+"']")) {
                   return true;
                }
            }
        }
        return false;
    }


    public void save(String filename) throws TransformerException {
        // write the DOM object to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(getDocument());
        StreamResult streamResult = new StreamResult(new File(filename));
        transformer.transform(domSource, streamResult);
    }

    private List<Element> getOrCreateElements(String name, String xpath, Element parent) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xpath);
        NodeList nodes = (NodeList) expression.evaluate(parent, XPathConstants.NODESET);

        List<Element> childElements = new ArrayList<Element>();
        if (nodes.getLength() == 0) {
            Document doc = this.getDocument();
            Element child = doc.createElement(name);
            parent.appendChild(child);
            childElements.add(child);
        } else {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                    childElements.add((Element) node);
            }
        }

        return childElements;
    }


    String PLUGIN_TEMPLATE = "plugin[artifactId='#ARTIFACT_ID#' and groupId='#GROUP_ID#']";

    private boolean isPluginExistInElement(String groupId, String artifactId, Element parent) throws XPathExpressionException {
        String xpath = PLUGIN_TEMPLATE.replace("#GROUP_ID#", groupId).replace("#ARTIFACT_ID#", artifactId);
        return isNodeExist(parent, xpath);
    }

    private boolean isNodeExist(Element parent, String xpathToNode) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xpathToNode);
        NodeList nodes = (NodeList) expression.evaluate(parent, XPathConstants.NODESET);
        return nodes.getLength() > 0;
    }

    public String getPomAsString() {
        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(getDocument().getDocumentElement());
            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Document getDocument() {
        if (document != null)
            return document;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(this.filename);
            document.getDocumentElement().normalize();

        } catch (SAXException | ParserConfigurationException | IOException e) {
            document = new EmptyDocument();
        }
        return document;
    }

    public String getFilename() {
        return filename;
    }
}
