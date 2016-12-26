package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.ConfigCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

/**
 * Executor for the 'config' command.
 */
public class ConfigCommandExecutor extends BaseCommandExecutor {

    private ConfigCommandArguments configCommandArguments;
    private BaseCommandArguments baseArgs;

    public ConfigCommandExecutor(Logger logger, ConfigCommandArguments configCommandArguments) {
        super(logger, configCommandArguments.getBaseArgs());
        this.configCommandArguments = configCommandArguments;
        baseArgs = configCommandArguments.getBaseArgs();
    }

    @Override
    public String getAdditionalArguments() {
        return "";
    }

    @Override
    public String createExecutionCommand() {
        validateArguments();
        String execCommand = resolvedJavaPath() + " -jar " + baseArgs.getAgentPath() + " " + getArgumentsLine();
        return execCommand.trim();
    }

    private void validateArguments(){
        String errors = "";
        if (baseArgs.getTokenData() == null || StringUtils.isNullOrEmpty(baseArgs.getTokenData().getToken())){
            errors += "Unable to find token. ";
        }
        if (StringUtils.isNullOrEmpty(baseArgs.getAppName())){
            errors += "Unable to find app name. ";
        }
        if (StringUtils.isNullOrEmpty(baseArgs.getBuildName())){
            errors += "Unable to find build name. ";
        }
        if (StringUtils.isNullOrEmpty(baseArgs.getBranchName())){
            errors += "Unable to find branch name. ";
        }

        if (StringUtils.isNullOrEmpty(configCommandArguments.getPackagesIncluded())){
            errors += "Unable to find monitored packages. ";
        }

        if ("".equals(errors)){
            throw new IllegalArgumentException(errors);
        }
    }


    private String getArgumentsLine(){
        String line = "-config";

        line += joinKeyValForLine("token", baseArgs.getTokenData().getToken());
        line += joinKeyValForLine("proxy", baseArgs.getProxy());
        line += joinKeyValForLine("appname", baseArgs.getAppName());
        line += joinKeyValForLine("buildname", baseArgs.getBuildName());
        line += joinKeyValForLine("branchname", baseArgs.getBranchName());
        line += joinKeyValForLine("packagesincluded", configCommandArguments.getPackagesIncluded());
        line += joinKeyValForLine("packagesexcluded", configCommandArguments.getPackagesExcluded());

        return line;
    }

    private String joinKeyValForLine(String key, String value){
        if (StringUtils.isNullOrEmpty(value)){
            return "";
        }
        return " -" + key + " " + value;
    }
}
