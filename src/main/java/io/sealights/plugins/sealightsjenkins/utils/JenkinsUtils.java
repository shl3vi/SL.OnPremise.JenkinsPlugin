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

        String logsUrl = envVars.get("PROMOTED_URL");
        if (StringUtils.isNullOrEmpty(logsUrl)){
            logsUrl = envVars.get("BUILD_URL");
        }
        metadata.put("logsUrl", logsUrl + "console");


        if (!StringUtils.isNullOrEmpty(envVars.get("PROMOTED_JOB_NAME"))){
            metadata.put("jobName", envVars.get("PROMOTED_JOB_NAME"));
        }else{
            metadata.put("jobName", envVars.get("JOB_NAME"));
        }

        return metadata;
    }

    public static String tryGetEnvVariable(EnvVars envVars, String envVarKey){
        // check if its jenkins environment variable in the format of '${<some_key>}'
        if (envVarKey != null && envVarKey.startsWith("${") && envVarKey.endsWith("}")){
            // get '<some_key>' from '${<some_key>}'
            String resolvedEnvKey = envVarKey.substring(2, envVarKey.length()-1);

            resolvedEnvKey = envVars.get(resolvedEnvKey);
            if (!StringUtils.isNullOrEmpty(resolvedEnvKey)){
                return resolvedEnvKey;
            }
        }
        return envVarKey;
    }

}
