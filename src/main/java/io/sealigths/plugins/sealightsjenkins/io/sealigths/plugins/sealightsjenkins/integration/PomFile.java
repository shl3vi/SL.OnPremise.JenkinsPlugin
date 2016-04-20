package io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration;

import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadav on 4/18/2016.
 */
public class PomFile {

    private String filename;
    private Document document;

    public PomFile(String filename) {
        this.filename = filename;
    }

    public List<String> getProfileIds() {
        List<String> profiles = new ArrayList<>();
        return profiles;
    }

    public boolean isPluginExist(String groupId, String artifactId) {
        return isPluginExist(groupId, artifactId, null);
    }

    public void addPlugin(String pluginBodyAsXml)
    {
        Document doc = this.getDocument();
        addPlugin(pluginBodyAsXml, doc.getDocumentElement());
    }

    public void addPlugin(String pluginBodyAsXml, Element parentElement)
    {
        Element buildElement = getOrCreateElement("build", parentElement);
        Element pluginsElement = getOrCreateElement("plugins", buildElement);
        //Element pluginElement = getDocument().createElement("plugin");
        try {
            String xml = "<plugin>"+pluginBodyAsXml+"</plugin>";
            Element pluginElement = (Element) DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))))
                    .getDocumentElement();

            pluginElement = (Element) document.importNode(pluginElement, true);
            pluginsElement.appendChild(pluginElement);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }



    public void save(String filename) throws TransformerException {
        // write the DOM object to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(getDocument());
        StreamResult streamResult = new StreamResult(new File(filename));
        transformer.transform(domSource, streamResult);
    }

    private Element getOrCreateElement(String name, Element parent) {
        NodeList childElements = parent.getElementsByTagName(name);
        Element childElement;
        if (childElements == null || childElements.getLength() == 0)
        {
            Document doc = this.getDocument();
            childElement = doc.createElement(name);
            parent.appendChild(childElement);
        }
        else
        {
            childElement = (Element) childElements.item(0);
        }

        return childElement;
    }


    public boolean isPluginExist(String groupId, String artifactId, String version) {
        Node pluginNode = getPluginNode(groupId, artifactId, version);
        Boolean isExist = pluginNode != null;
        return isExist;
    }

    public Node getPluginNode(String groupId, String artifactId, String version) {
        Document doc = this.getDocument();
        NodeList allPluginNodes = doc.getElementsByTagName("plugin");
        for (int i = 0; i < allPluginNodes.getLength(); i++) {
            Node pluginNode = allPluginNodes.item(i);
            if (isPlugin(groupId, artifactId, version, pluginNode))
                return pluginNode;
        }
        return null;
    }

    private boolean isPlugin(String groupId, String artifactId, String version, Node pluginNode) {
        //Get the node which holds the plugin information (groupId, artifactId, version, etc).
        NodeList pluginInformation = pluginNode.getChildNodes();
        boolean sameGroupId = false;
        boolean sameArtifactId = false;
        boolean sameVersion = false;
        for (int j = 0; j < pluginInformation.getLength(); j++) {
            Node child = pluginInformation.item(j);
            String nodeName = child.getNodeName();
            String nodeContent = child.getTextContent();
            if (nodeName.equalsIgnoreCase("groupId")) {
                sameGroupId = nodeContent.equalsIgnoreCase(groupId);
            } else if (nodeName.equalsIgnoreCase("artifactId")) {
                sameArtifactId = nodeContent.equalsIgnoreCase(artifactId);
            } else if (nodeName.equalsIgnoreCase("version")) {
                sameVersion = nodeContent.equalsIgnoreCase(version);
            }
        }

        if (sameGroupId && sameArtifactId && (sameVersion || version == null))
            return true;
        return false;
    }

    public String getPluginVersion(String groupId, String artifactId) {
        Node pluginNode = getPluginNode(groupId, artifactId, null);
        if (pluginNode == null)
            return "";

        NodeList pluginInformation = pluginNode.getChildNodes();
        for (int j = 0; j < pluginInformation.getLength(); j++) {
            Node child = pluginInformation.item(j);
            String nodeName = child.getNodeName();
            if (nodeName.equalsIgnoreCase("version")) {
                return child.getTextContent();
            }
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


}
