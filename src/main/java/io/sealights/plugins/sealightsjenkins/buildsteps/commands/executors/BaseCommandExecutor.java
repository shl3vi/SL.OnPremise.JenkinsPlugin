package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;


import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StreamUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

/**
 * Abstract class for command executors.
 */
public abstract class BaseCommandExecutor implements ICommandExecutor {

    protected Logger logger;
    private BaseCommandArguments baseArgs;

    public BaseCommandExecutor(Logger logger, BaseCommandArguments baseArgs) {
        this.logger = logger;
        this.baseArgs = baseArgs;
    }

    public boolean execute() {
        try {
            String execCommand = createExecutionCommand();

            // Run a java app in a separate system process
            logger.info("About to execute command: " + execCommand);
            Process proc = Runtime.getRuntime().exec(execCommand);

            printStreams(proc);

        } catch (Exception e) {
            logger.error("Unable to perform '" + getCommandName() + "' command. Error: ", e);
        }

        return true;
    }

    protected void printStreams(Process proc) {
        // Receive the process output
        String outputInfo = StreamUtils.toString(proc.getInputStream());
        String outputErrors = StreamUtils.toString(proc.getErrorStream());
        logger.info("Process ended with exit code: " + proc.exitValue());
        if (!StringUtils.isNullOrEmpty(outputInfo)) {
            logger.info("Process output:");
            logger.info(outputInfo);
        }
        if (!StringUtils.isNullOrEmpty(outputErrors)) {
            logger.info("Process errors output:");
            logger.error(outputErrors);
        }
    }


    public String createExecutionCommand() {
        String arguments = getBaseArgumentsLine() + getAdditionalArguments();
        String execCommand = resolvedJavaPath() + " -jar " + baseArgs.getAgentPath() + " " + getCommandName() + " " + arguments;
        return execCommand.trim();
    }

    private String getCommandName() {
        return baseArgs.getMode().getCurrentMode().getName();
    }

    public abstract String getAdditionalArguments();

    protected String getBaseArgumentsLine() {
        StringBuilder sb = new StringBuilder();

        if (baseArgs.getTokenData() != null){
            addArgumentKeyVal(sb, "token", baseArgs.getTokenData().getToken());
        }else{
            addArgumentKeyVal(sb, "customerid", baseArgs.getCustomerId());
            addArgumentKeyVal(sb, "server", baseArgs.getUrl());
        }

        addArgumentKeyVal(sb, "appname", baseArgs.getAppName());
        addArgumentKeyVal(sb, "branchname", baseArgs.getBranchName());
        addArgumentKeyVal(sb, "environment", baseArgs.getEnvironment());
        addArgumentKeyVal(sb, "proxy", baseArgs.getProxy());
        addArgumentKeyVal(sb, "buildname", baseArgs.getBuildName());

        return sb.toString();
    }

    protected void addArgumentKeyVal(StringBuilder sb, String key, String val) {
        if (StringUtils.isNullOrEmpty(val)) {
            return;
        }
        sb.append("-");
        sb.append(key);
        sb.append(" ");
        sb.append("\"" + val + "\"");
        sb.append(" ");
    }

    private String resolvedJavaPath() {
        if (!StringUtils.isNullOrEmpty(baseArgs.getJavaPath()))
            return baseArgs.getJavaPath();

        return "java";
    }
}
