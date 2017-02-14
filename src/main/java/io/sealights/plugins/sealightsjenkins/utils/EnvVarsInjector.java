package io.sealights.plugins.sealightsjenkins.utils;

import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.model.VariableInjectionAction;

import java.util.HashMap;
import java.util.Set;

/**
 * This class inject environment variables to the job's current build
 */
public class EnvVarsInjector {

    private HashMap<String, String> additionalEnvVars = new HashMap<String, String>();
    private AbstractBuild<?, ?> build;
    private Logger logger;

    public EnvVarsInjector(AbstractBuild<?, ?> build, Logger logger) {
        this.build = build;
        this.logger = logger;
    }

    public void addEnvVariableToBuild(String key, String val) {
        if (key == null) {
            throw new NullPointerException("Trying to add 'null' as key to the environment variables.");
        }

        additionalEnvVars.put(key, val);
    }

    public void addEnvVariablesToBuild(HashMap<String, String> additionalEnvVars) {
        additionalEnvVars.putAll(additionalEnvVars);
    }

    public void inject(){
        VariableInjectionAction variableInjectionAction = new VariableInjectionAction(this.additionalEnvVars, logger);
        printVariables();
        build.addAction(variableInjectionAction);
    }

    private void printVariables() {
        logger.info("Injecting environment variables:");
        Set<String> keySet = additionalEnvVars.keySet();
        for (String key: keySet){
            logger.info(key + " : " + additionalEnvVars.get(key));
        }
    }
}
