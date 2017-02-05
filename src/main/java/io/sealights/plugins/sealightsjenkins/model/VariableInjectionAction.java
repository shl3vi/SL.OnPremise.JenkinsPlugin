package io.sealights.plugins.sealightsjenkins.model;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by shahar on 2/2/2017.
 */
public class VariableInjectionAction implements EnvironmentContributingAction {

    private Map<String, String> additionalEnvVars;

    public VariableInjectionAction(Map<String, String> additionalEnvVars) {
        this.additionalEnvVars = cleanNullPairs(additionalEnvVars);
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> abstractBuild, EnvVars envVars) {
        if (envVars == null) {
            return;
        }
        envVars.putAll(additionalEnvVars);
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "SeaLights.VariableInjectionAction";
    }

    @Override
    public String getUrlName() {
        return null;
    }

    private Map<String, String> cleanNullPairs(Map<String, String> additionalEnvVars) {
        Set<String> entries = additionalEnvVars.keySet();
        for (String key : entries) {
            if (StringUtils.isNullOrEmpty(key) || StringUtils.isNullOrEmpty(additionalEnvVars.get(key)))
                additionalEnvVars.remove(key);
        }

        return additionalEnvVars;
    }
}
