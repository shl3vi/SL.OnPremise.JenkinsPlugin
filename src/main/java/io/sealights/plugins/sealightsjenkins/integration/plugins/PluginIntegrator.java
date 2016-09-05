package io.sealights.plugins.sealightsjenkins.integration.plugins;

import io.sealights.plugins.sealightsjenkins.integration.PomFile;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * An abstract class for classes that integrates to plugins.
 */
public abstract class PluginIntegrator {

    protected Logger logger;
    protected PomFile pomFile;

    public PluginIntegrator(Logger logger, PomFile pomFile) {
        this.logger = logger;
        this.pomFile = pomFile;
    }

    protected abstract String artifactId();

    protected abstract String groupId();

    public final String pluginDescriptor(){
        return groupId()+":"+artifactId();
    }

    protected final String skipPropertyName() { return "sealights."+artifactId()+".skip";}

    public final void integrateSafe(){
        try{
            integrate();
        }catch (Exception e){
            logger.error("Unable to integrate to plugin '"+pluginDescriptor()+"'. Error:", e);
        }
    }

    protected boolean shouldSkipIntegration() throws XPathExpressionException {
        String skipPropertyName = skipPropertyName();
        List<Element> propertyElementList = pomFile.getProperties();

        for (Element propertyElement : propertyElementList) {
            if (!skipPropertyName.equals(propertyElement.getTagName()))
                continue;
            if ("true".equalsIgnoreCase(propertyElement.getTextContent()))
                return true;
        }

        return false;
    }

    protected abstract void integrate() throws Exception;

    public abstract boolean isAlreadyIntegrated();
}