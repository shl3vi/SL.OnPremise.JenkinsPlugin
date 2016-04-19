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
                    .parse(new ByteArrayInputStream(xml.getBytes()))
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
        } catch (Exception e) {
            document = new EmptyDocument();
        }

        return document;
    }

    private class EmptyDocument implements Document {

        @Override
        public DocumentType getDoctype() {
            return null;
        }

        @Override
        public DOMImplementation getImplementation() {
            return null;
        }

        @Override
        public Element getDocumentElement() {
            return null;
        }

        @Override
        public Element createElement(String tagName) throws DOMException {
            return null;
        }

        @Override
        public DocumentFragment createDocumentFragment() {
            return null;
        }

        @Override
        public Text createTextNode(String data) {
            return null;
        }

        @Override
        public Comment createComment(String data) {
            return null;
        }

        @Override
        public CDATASection createCDATASection(String data) throws DOMException {
            return null;
        }

        @Override
        public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
            return null;
        }

        @Override
        public Attr createAttribute(String name) throws DOMException {
            return null;
        }

        @Override
        public EntityReference createEntityReference(String name) throws DOMException {
            return null;
        }

        @Override
        public NodeList getElementsByTagName(String tagname) {
            return null;
        }

        @Override
        public Node importNode(Node importedNode, boolean deep) throws DOMException {
            return null;
        }

        @Override
        public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
            return null;
        }

        @Override
        public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
            return null;
        }

        @Override
        public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
            return null;
        }

        @Override
        public Element getElementById(String elementId) {
            return null;
        }

        @Override
        public String getInputEncoding() {
            return null;
        }

        @Override
        public String getXmlEncoding() {
            return null;
        }

        @Override
        public boolean getXmlStandalone() {
            return false;
        }

        @Override
        public void setXmlStandalone(boolean xmlStandalone) throws DOMException {

        }

        @Override
        public String getXmlVersion() {
            return null;
        }

        @Override
        public void setXmlVersion(String xmlVersion) throws DOMException {

        }

        @Override
        public boolean getStrictErrorChecking() {
            return false;
        }

        @Override
        public void setStrictErrorChecking(boolean strictErrorChecking) {

        }

        @Override
        public String getDocumentURI() {
            return null;
        }

        @Override
        public void setDocumentURI(String documentURI) {

        }

        @Override
        public Node adoptNode(Node source) throws DOMException {
            return null;
        }

        @Override
        public DOMConfiguration getDomConfig() {
            return null;
        }

        @Override
        public void normalizeDocument() {

        }

        @Override
        public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
            return null;
        }

        @Override
        public String getNodeName() {
            return null;
        }

        @Override
        public String getNodeValue() throws DOMException {
            return null;
        }

        @Override
        public void setNodeValue(String nodeValue) throws DOMException {

        }

        @Override
        public short getNodeType() {
            return 0;
        }

        @Override
        public Node getParentNode() {
            return null;
        }

        @Override
        public NodeList getChildNodes() {
            return null;
        }

        @Override
        public Node getFirstChild() {
            return null;
        }

        @Override
        public Node getLastChild() {
            return null;
        }

        @Override
        public Node getPreviousSibling() {
            return null;
        }

        @Override
        public Node getNextSibling() {
            return null;
        }

        @Override
        public NamedNodeMap getAttributes() {
            return null;
        }

        @Override
        public Document getOwnerDocument() {
            return null;
        }

        @Override
        public Node insertBefore(Node newChild, Node refChild) throws DOMException {
            return null;
        }

        @Override
        public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
            return null;
        }

        @Override
        public Node removeChild(Node oldChild) throws DOMException {
            return null;
        }

        @Override
        public Node appendChild(Node newChild) throws DOMException {
            return null;
        }

        @Override
        public boolean hasChildNodes() {
            return false;
        }

        @Override
        public Node cloneNode(boolean deep) {
            return null;
        }

        @Override
        public void normalize() {

        }

        @Override
        public boolean isSupported(String feature, String version) {
            return false;
        }

        @Override
        public String getNamespaceURI() {
            return null;
        }

        @Override
        public String getPrefix() {
            return null;
        }

        @Override
        public void setPrefix(String prefix) throws DOMException {

        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public boolean hasAttributes() {
            return false;
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public short compareDocumentPosition(Node other) throws DOMException {
            return 0;
        }

        @Override
        public String getTextContent() throws DOMException {
            return null;
        }

        @Override
        public void setTextContent(String textContent) throws DOMException {

        }

        @Override
        public boolean isSameNode(Node other) {
            return false;
        }

        @Override
        public String lookupPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public boolean isDefaultNamespace(String namespaceURI) {
            return false;
        }

        @Override
        public String lookupNamespaceURI(String prefix) {
            return null;
        }

        @Override
        public boolean isEqualNode(Node arg) {
            return false;
        }

        @Override
        public Object getFeature(String feature, String version) {
            return null;
        }

        @Override
        public Object setUserData(String key, Object data, UserDataHandler handler) {
            return null;
        }

        @Override
        public Object getUserData(String key) {
            return null;
        }
    }

}
