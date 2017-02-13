package io.sealights.plugins.sealightsjenkins.model;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by shahar on 2/2/2017.
 */
public class VariableInjectionAction implements EnvironmentContributingAction {

    private Map<String, String> additionalEnvVars;
    private Logger logger;

    public VariableInjectionAction(Map<String, String> additionalEnvVars, Logger logger) {
        this.additionalEnvVars = additionalEnvVars;
        this.logger = logger;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> abstractBuild, EnvVars envVars) {
        try {
            if (envVars == null) {
                return;
            }
            cleanNullPairs();
            envVars.putAll(additionalEnvVars);
        } catch (Exception e) {
            logger.error("Failed to inject SeaLights additional environment variables. Error: ", e);
        }
    }

    /************************
    *  START - Making sure to add nothing to the task bar at the UI's left side.
    * **********************/
    public final String getIconFileName() {
        return null;
    }

    public final String getDisplayName() {
        return null;
    }

    public final String getUrlName() {
        return null;
    }
    /************************
     *  END - Making sure to add nothing to the task bar at the UI's left side.
     * **********************/

    private Map<String, String> cleanNullPairs() {
        Set<String> entries = new HashSet<>(additionalEnvVars.keySet());
        for (String key : entries) {
            if (StringUtils.isNullOrEmpty(key))
                additionalEnvVars.remove(key);
        }

        return additionalEnvVars;
    }
}
