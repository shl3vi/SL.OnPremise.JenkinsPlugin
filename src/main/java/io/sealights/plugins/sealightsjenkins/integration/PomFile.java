package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.integration.plugins.SealightsMavenPluginHelper;
import io.sealights.plugins.sealightsjenkins.integration.plugins.external.JMeterPluginHelper;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
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
            return PomXmlUtils.isPluginExistInElement(artifactId, getDocumentElement(), true);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void integrate(MavenIntegrationInfo mavenIntegrationInfo) {
        try {
            integrateToAllPlugins(mavenIntegrationInfo);
            verifySurefireArgLineModification(getDocumentElement());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private void integrateToAllPlugins(MavenIntegrationInfo mavenIntegrationInfo)
            throws XPathExpressionException{
        Document pomDoc = getDocument();

        SealightsMavenPluginHelper sealightsMavenPluginHelper = new SealightsMavenPluginHelper(log, mavenIntegrationInfo, pomDoc);
        sealightsMavenPluginHelper.integrate();

        JMeterPluginHelper jmeterPluginHelper = new JMeterPluginHelper(log, mavenIntegrationInfo, pomDoc);
        jmeterPluginHelper.integrate();

    }

    private void verifySurefireArgLineModification(Element docElement){
        if (docElement == null) {
            log.warning("Couldn't read pom file (documentElement is null) while trying to verify surefire 'argLine' modification.");
            return;
        }

        try {
            List<Element> pluginElements = PomXmlUtils.getElements("//plugin", docElement);
            for (Element plugin : pluginElements) {
                if (!PomXmlUtils.isNodeExist("./artifactId[.='maven-surefire-plugin']", plugin)) {
                    //Not a surefire plugin
                    continue;
                }
                List<Element> argLineElements = PomXmlUtils.getElements("./configuration/argLine", plugin);
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



    public boolean isValidPom() {

        Element documentElement = getDocumentElement();
        if (documentElement == null) {
            log.warning("Couldn't read pom file (documentElement is null).");
            return false;
        }

        try {
            List<Element> pluginElements = PomXmlUtils.getElements("//plugin", documentElement);
            for (Element element : pluginElements) {
                if (!PomXmlUtils.isNodeExist("./artifactId[.='maven-surefire-plugin']", element)) {
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
        List<Element> forkModeElements = PomXmlUtils.getElements("./configuration/parallel", surefirePlugin);
        return !forkModeElements.isEmpty();
    }

    private boolean isValidForkMode(Element surefirePlugin) throws XPathExpressionException {
        List<Element> forkModeElements = PomXmlUtils.getElements("./configuration/forkMode", surefirePlugin);
        if (forkModeElements.isEmpty())
            return true;

        Element forkMode = forkModeElements.get(0);
        String currentValue = forkMode.getTextContent();

        return !(
                ("perthread".equalsIgnoreCase(currentValue) && !isValidPerThreadForkMode(surefirePlugin))
                || "never".equalsIgnoreCase(currentValue));
    }

    private boolean isValidPerThreadForkMode(Element surefirePlugin) throws XPathExpressionException {
        List<Element> threadCountElements = PomXmlUtils.getElements("./configuration/threadCount", surefirePlugin);
        if (threadCountElements.isEmpty())
            //threadCount is '0' by default and its unsupported.
            return false;

        Element threadCountElement = threadCountElements.get(0);
        String currentValue = threadCountElement.getTextContent();
        return !"0".equals(currentValue);
    }

    private boolean isValidForkCount(Element surefirePlugin) throws XPathExpressionException {
        List<Element> forkCountElements = PomXmlUtils.getElements("./configuration/forkCount", surefirePlugin);
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

    private Element getDocumentElement(){
        return getDocument().getDocumentElement();
    }

    protected Document getDocumentInternal() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(this.filename);
        return doc;
    }

}
