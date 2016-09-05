package io.sealights.plugins.sealightsjenkins.integration.plugins;

/**
 * An abstract class for classes that integrates to plugins.
 */
public abstract class PluginIntegrator {

    protected abstract String artifactId();

    protected abstract String groupId();

    protected final String pluginDescriptor(){
        return groupId()+":"+artifactId();
    }

    protected final String skipPropertyName() { return "sealights."+artifactId()+".skip";}

    protected abstract void integrate();

}