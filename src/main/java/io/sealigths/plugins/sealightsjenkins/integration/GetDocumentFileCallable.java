package io.sealigths.plugins.sealightsjenkins.integration;


import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Nadav on 5/15/2016.
 */

public class GetDocumentFileCallable implements FilePath.FileCallable<Document> {
    private static final long serialVersionUID = 1L;

    @Override
    public Document invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file);
        } catch (SAXException e) {
            return new EmptyDocument();
        } catch (ParserConfigurationException e) {
            return new EmptyDocument();
        }
    }


    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }
}