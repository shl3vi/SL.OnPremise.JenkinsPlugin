package io.sealights.plugins.sealightsjenkins.integration;

import hudson.FilePath;
import hudson.model.Computer;
import hudson.remoting.VirtualChannel;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Nadav on 5/21/2016.
 */
public class JenkinsPomFile extends PomFile {
    public JenkinsPomFile(String filename, Logger log) {
        super(filename, log);
    }

    @Override
    protected void saveInternal(String filename, Transformer transformer, DOMSource domSource) throws TransformerException, IOException, InterruptedException {
        VirtualChannel channel = Computer.currentComputer().getChannel();
        FilePath filePath = new FilePath(channel, filename);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(outputStream);
        transformer.transform(domSource, streamResult);

        String str = new String( outputStream.toByteArray(), "UTF-8");

        String result = filePath.act(new SaveFileCallable(str));
        if (!StringUtils.isNullOrEmpty(result))
            log.info("save - Result:" + result);
    }

    @Override
    protected Document getDocumentInternal() throws IOException, InterruptedException, SAXException, ParserConfigurationException {

        VirtualChannel channel = Computer.currentComputer().getChannel();
        FilePath fp = new FilePath(channel, this.filename);
        Document doc = fp.act(new GetDocumentFileCallable());
        return doc;
    }
}
