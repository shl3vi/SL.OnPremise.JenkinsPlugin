package io.sealights.plugins.sealightsjenkins.utils;

import hudson.EnvVars;
import hudson.Util;
import hudson.model.AbstractBuild;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Nadav on 6/7/2016.
 */
public class JenkinsUtils {
    public String expandPathVariable(AbstractBuild<?, ?> build, String path)
    {
        String[] tokens = path.split(Pattern.quote(File.separator.toString()));
        for(int i=0; i< tokens.length; i++){
            tokens[i] = expandVariable(build, tokens[i]);
        }
        path = StringUtils.join(tokens, File.separatorChar);

        return path;
    }

    public String expandVariable(AbstractBuild<?, ?> build, String variable) {
        return Util.replaceMacro(variable, build.getBuildVariables());
    }

    public static Map<String, String> createMetadataFromEnvVars(EnvVars envVars){
        Map <String, String> metadata = new HashMap<>();
        metadata.put("jobUrl", envVars.get("JOB_URL"));
        return metadata;
    }
}
