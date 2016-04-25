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
        List<String> profiles = new ArrayList<>();
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
            List<Element> pluginsElements = getOrCreateElements("plugins", "plugins", buildElement);
            try {

                for (int i = 0; i < pluginsElements.size(); i++) {
                    Element pluginsElement = pluginsElements.get(i);

                    if (!isPluginExistInElement(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID, pluginsElement)){
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

    String SUREFIRE_PLUGIN = "//build/plugins/plugin/artifactId[.='maven-surefire-plugin']/parent::plugin";
    public void addEventListener(String additionalClassPath, String properties){
        Element documentElement = getDocument().getDocumentElement();

        try {
            List<Element> surefireElements = getOrCreateElements("plugin", SUREFIRE_PLUGIN, documentElement);
            for (Element surefireElement : surefireElements) {
                List<Element> configurationElements = getOrCreateElements("configuration", "/configuration", surefireElement);
                for (Element configurationElement : configurationElements) {

            Element additionalClassPathElement = (Element) DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(additionalClassPath.getBytes(Charset.forName("UTF-8"))))
                    .getDocumentElement();

                    Element propertiesElement = (Element) DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(properties.getBytes(Charset.forName("UTF-8"))))
                            .getDocumentElement();

            additionalClassPathElement = (Element) document.importNode(additionalClassPathElement, true);
                    propertiesElement = (Element) document.importNode(propertiesElement, true);

            configurationElement.appendChild(additionalClassPathElement);
                    configurationElement.appendChild(propertiesElement);
                }
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

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
        String xpath = PLUGIN_TEMPLATE.replace("#GROUP_ID#", groupId).replace("#ARTIFACT_ID#",artifactId);

        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xpath);
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


}
