package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.utils.Logger;
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
            verifySurefireArgLineModification(docElement);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private void verifySurefireArgLineModification(Element docElement){
        if (docElement == null) {
            log.warning("Couldn't read pom file (documentElement is null) while trying to verify surefire 'argLine' modification.");
            return;
        }

        try {
            List<Element> pluginElements = getElements("//plugin", docElement);
            for (Element plugin : pluginElements) {
                if (!isNodeExist(plugin, "./artifactId[.='maven-surefire-plugin']")) {
                    //Not a surefire plugin
                    continue;
                }
                List<Element> argLineElements = getElements("./configuration/argLine", plugin);
                if (argLineElements.size() > 0){
                    modifySurefireArgLine(argLineElements.get(0));
                }
            }
        } catch (Exception e) {
            log.error("Failed while trying to verify surefire 'argLine' modification. Error:", e);
        }
    }

    private void modifySurefireArgLine(Element argLine) {
        String currentValue = argLine.getTextContent();
        if (!currentValue.contains("${argLine}")) {
            currentValue = "${argLine} " + currentValue;
            argLine.setTextContent(currentValue);
        }
    }

    private void addPluginToAllProfiles(String pluginBodyAsXml, Element parent) throws XPathExpressionException {
        List<Element> profilesList = getElements("profiles", parent);
        for (Element profiles : profilesList) {
            List<Element> profileList = getElements("profile", profiles);
            for (Element profile : profileList) {
                addPlugin(pluginBodyAsXml, profile);
            }
        }
    }


    private void addPlugin(String pluginBodyAsXml, Element parentElement) throws XPathExpressionException {
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

            for (Element pluginsElement : pluginsElements) {
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

    public boolean isValidPom() {

        Element documentElement = getDocument().getDocumentElement();
        if (documentElement == null) {
            log.warning("Couldn't read pom file (documentElement is null).");
            return false;
        }

        try {
            List<Element> pluginElements = getElements("//plugin", documentElement);
            for (Element element : pluginElements) {
                if (!isNodeExist(element, "./artifactId[.='maven-surefire-plugin']")) {
                    //Not a surefire plugin
                    continue;
                }
                if (!isSurefireValid(element)) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Failed while trying to validate the pom. Error:", e);
            return false;
        }
        return true;
    }

    private boolean isSurefireValid(Element surefirePlugin) throws XPathExpressionException {
        if (!isValidForkCount(surefirePlugin)) {
            log.warning("Found an unsupported 'forkCount' value of SureFire. Value cannot be '0'.");
            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Found an unsupported 'forkCount' value of SureFire. Value cannot be '0'.");
            return false;
        }
        if (!isValidForkMode(surefirePlugin)) {
            log.warning("Found an unsupported 'forkMode' value of SureFire. Value cannot be 'never' or combination of 'perthread' with 'threadCount' of 0.");
            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Found an unsupported 'forkMode' value of SureFire. Value cannot be 'never' or combination of 'perthread' with 'threadCount' of 0..");
            return false;
        }

        if (isParallelExist(surefirePlugin)){
            log.warning("Found an unsupported 'parallel' value of SureFire.");
            System.err.println("[SeaLights Jenkins Plugin] - WARNING - Found an unsupported 'parallel' tag of SureFire.");
        }
        return true;
    }

    private boolean isParallelExist(Element surefirePlugin) throws XPathExpressionException {
        List<Element> forkModeElements = getElements("./configuration/parallel", surefirePlugin);
        return !forkModeElements.isEmpty();
    }

    private boolean isValidForkMode(Element surefirePlugin) throws XPathExpressionException {
        List<Element> forkModeElements = getElements("./configuration/forkMode", surefirePlugin);
        if (forkModeElements.isEmpty())
            return true;

        Element forkMode = forkModeElements.get(0);
        String currentValue = forkMode.getTextContent();

        return !(
                ("perthread".equalsIgnoreCase(currentValue) && !isValidPerThreadForkMode(surefirePlugin))
                || "never".equalsIgnoreCase(currentValue));
    }

    private boolean isValidPerThreadForkMode(Element surefirePlugin) throws XPathExpressionException {
        List<Element> threadCountElements = getElements("./configuration/threadCount", surefirePlugin);
        if (threadCountElements.isEmpty())
            //threadCount is '0' by default and its unsupported.
            return false;

        Element threadCountElement = threadCountElements.get(0);
        String currentValue = threadCountElement.getTextContent();
        return !"0".equals(currentValue);
    }

    private boolean isValidForkCount(Element surefirePlugin) throws XPathExpressionException {
        List<Element> forkCountElements = getElements("./configuration/forkCount", surefirePlugin);
        if (forkCountElements.isEmpty())
            return true;

        Element forkCount = forkCountElements.get(0);
        String currentValue = forkCount.getTextContent();
        return !"0".equals(currentValue);
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

    private boolean isPluginExistInElement(String artifactId, Element parent, boolean includeAllDescendants) throws XPathExpressionException {
        String xpath = "plugin[artifactId='#ARTIFACT_ID#']";
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
