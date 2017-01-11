package io.sealights.plugins.sealightsjenkins.buildsteps.commands.utils;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.CommandBuildName;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandBuildNamingStrategy;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

public class BuildNameResolver {

    public String getFinalBuildName(
            AbstractBuild<?, ?> build, EnvVars envVars, CommandBuildName buildName, Logger logger) throws IllegalStateException {

        String finalBuildName = null;

        if (CommandBuildNamingStrategy.LATEST_BUILD.equals(buildName.getBuildNamingStrategy()) ||
                CommandBuildNamingStrategy.EMPTY_BUILD.equals(buildName.getBuildNamingStrategy())) {
            return null;
        }

        if (CommandBuildNamingStrategy.MANUAL.equals(buildName.getBuildNamingStrategy())) {
            finalBuildName = getManualBuildName(buildName);

        } else if (CommandBuildNamingStrategy.JENKINS_UPSTREAM.equals(buildName.getBuildNamingStrategy())) {
            CommandBuildName.UpstreamBuildName upstream = (CommandBuildName.UpstreamBuildName) buildName;
            String upstreamProjectName = upstream.getUpstreamProjectName();
            finalBuildName = JenkinsUtils.getUpstreamBuildName(build, upstreamProjectName, logger);
        }

        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            return String.valueOf(build.getNumber());
        }

        return JenkinsUtils.tryGetEnvVariable(envVars, finalBuildName);
    }

    private String getManualBuildName(CommandBuildName buildName) {
        CommandBuildName.ManualBuildName manual = (CommandBuildName.ManualBuildName) buildName;
        String insertedBuildName = manual.getInsertedBuildName();
        return insertedBuildName;
    }
}
