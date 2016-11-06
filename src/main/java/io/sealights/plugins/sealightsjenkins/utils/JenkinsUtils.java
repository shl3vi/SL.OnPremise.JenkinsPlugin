package io.sealights.plugins.sealightsjenkins.utils;

import hudson.EnvVars;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Cause;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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

    /**
     * @param envVarKey a potential environment variable
     * @return true if its jenkins environment variable in the format of '${<some_key>}'
     */
    private static boolean isEnvVariable(String envVarKey){
        return envVarKey != null && envVarKey.startsWith("${") && envVarKey.endsWith("}");
    }

    public static String tryGetEnvVariable(EnvVars envVars, String envVarKey){
        if (envVarKey == null)
            return "";

        if (isEnvVariable(envVarKey)){
            // get '<some_key>' from '${<some_key>}'
            String resolvedEnvKey = envVarKey.substring(2, envVarKey.length()-1);

            resolvedEnvKey = envVars.get(resolvedEnvKey);
            if (!StringUtils.isNullOrEmpty(resolvedEnvKey)){
                return resolvedEnvKey;
            }
        }
        return envVarKey;
    }

    public static String getUpstreamBuildName(AbstractBuild<?, ?> build, String upstreamProjectName, Logger logger) {
        String finalBuildName = getBuildNumberFromUpstreamBuild(build.getCauses(), upstreamProjectName);
        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            logger.warning("Couldn't find build number for " + upstreamProjectName + ". Using this job's build name.");
            return null;
        }

        logger.info("Upstream project: " + upstreamProjectName + " # " + finalBuildName);
        return finalBuildName;
    }

    private static String getBuildNumberFromUpstreamBuild(List<Cause> causes, String trigger) {
        String buildNum = null;
        for (Cause c : causes) {
            if (c instanceof Cause.UpstreamCause) {
                buildNum = checkCauseRecursivelyForBuildNumber((Cause.UpstreamCause) c, trigger);
                if (!StringUtils.isNullOrEmpty(buildNum)) {
                    break;
                }
            }
        }
        return buildNum;
    }

    private static String checkCauseRecursivelyForBuildNumber(Cause.UpstreamCause cause, String trigger) {
        if (trigger.equals(cause.getUpstreamProject())) {
            return String.valueOf(cause.getUpstreamBuild());
        }

        return getBuildNumberFromUpstreamBuild(cause.getUpstreamCauses(), trigger);
    }

}
