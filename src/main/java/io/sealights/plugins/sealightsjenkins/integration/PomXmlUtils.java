package io.sealights.plugins.sealightsjenkins.integration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by shahar on 8/29/2016.
 */
public class PomXmlUtils {
    public static boolean isPluginExistInElement(String artifactId, Element parent, boolean includeAllDescendants) throws XPathExpressionException {
        String xpath = "plugin[artifactId='#ARTIFACT_ID#']";
        if (includeAllDescendants)
            xpath = "//" + xpath;

        xpath = xpath.replace("#ARTIFACT_ID#", artifactId);
        return isNodeExist(xpath, parent);
    }

    public static List<Element> getPluginsOccurrencesInParent(String artifactId, Element parent, boolean includeAllDescendants) throws XPathExpressionException {
        String xpath = "plugin[artifactId='#ARTIFACT_ID#']";
        if (includeAllDescendants)
            xpath = "//" + xpath;

        xpath = xpath.replace("#ARTIFACT_ID#", artifactId);
        List<Element> plugins = getElements(xpath, parent);
        return plugins;
    }

    public static boolean isNodeExist(String xpath, Element parent) throws XPathExpressionException {
        NodeList nodes = getNodeList(xpath, parent);
        return nodes.getLength() > 0;
    }

    private static NodeList getNodeList(String xpath, Element parent) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xpath);
        return (NodeList) expression.evaluate(parent, XPathConstants.NODESET);
    }

    public static List<Element> getElements(String xpath, Element parent) throws XPathExpressionException {
        NodeList nodes = getNodeList(xpath, parent);
        List<Element> childElements = new ArrayList<Element>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                childElements.add((Element) node);
        }

        return childElements;
    }

    private static List<Element> getOrCreateElements(String name, String xpath, Element parent, Document pomDoc) throws XPathExpressionException {

        List<Element> childElements = getElements(xpath, parent);

        if (childElements.isEmpty()) {
            Element child = pomDoc.createElement(name);
            parent.appendChild(child);
            childElements.add(child);
        }

        return childElements;
    }

    public static List<Element> getOrCreateElements(String nameAndXpath, Element parent, Document pomDoc) throws XPathExpressionException {
        return getOrCreateElements(nameAndXpath, nameAndXpath, parent, pomDoc);
    }

    public static void verifyPluginsElement(String pluginBodyAsXml, Element parentElement, Document pomDoc) throws XPathExpressionException {
        List<Element> pluginsElements = getOrCreateElements("plugins", parentElement, pomDoc);
        try {

            for (Element pluginsElement : pluginsElements) {
                addPluginToPluginsElement(pluginBodyAsXml, pluginsElement, pomDoc);
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void addPluginToPluginsElement(String pluginBodyAsXml, Element pluginsElement, Document pomDoc) throws SAXException, IOException, ParserConfigurationException {
        String xml = "<plugin>" + pluginBodyAsXml + "</plugin>";
        Element pluginElement =  createElement(xml, pomDoc);
        pluginsElement.appendChild(pluginElement);
    }

    public static Element createElement(String XmlAsString, Document pomDoc) throws ParserConfigurationException, IOException, SAXException {
        Element element = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(XmlAsString.getBytes(Charset.forName("UTF-8"))))
                .getDocumentElement();
        element = (Element) pomDoc.importNode(element, true);
        return element;
    }

}
