package io.sealights.plugins.sealightsjenkins.utils;

import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.model.VariableInjectionAction;

import java.util.HashMap;

/**
 * Created by shahar on 2/2/2017.
 */
public class EnvVarsInjector {

    private HashMap<String, String> additionalEnvVars = new HashMap<String, String>();

    public void addEnvVariableToBuild(String key, String val) {
        if (key == null || val == null)
            return;

        additionalEnvVars.put(key, val);
    }

    public void addEnvVariablesToBuild(HashMap<String, String> additionalEnvVars) {
        additionalEnvVars.putAll(additionalEnvVars);
    }

    public void inject(AbstractBuild<?, ?> build){
        VariableInjectionAction variableInjectionAction = new VariableInjectionAction(this.additionalEnvVars);
        build.addAction(variableInjectionAction);
    }
}
