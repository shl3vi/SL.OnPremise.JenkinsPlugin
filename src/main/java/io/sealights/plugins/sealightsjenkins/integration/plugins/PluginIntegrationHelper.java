package io.sealights.plugins.sealightsjenkins.integration.plugins;

/**
 * Created by shahar on 8/29/2016.
 */
public abstract class PluginIntegrationHelper {

    protected abstract String artifactId();
    protected abstract String groupId();
    protected final String pluginDescriptor(){
        return groupId()+":"+artifactId();
    }
    protected abstract void integrate();
}