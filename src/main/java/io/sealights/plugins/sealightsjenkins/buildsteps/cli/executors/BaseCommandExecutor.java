package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;


import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.ProcessUtils;
import io.sealights.plugins.sealightsjenkins.utils.StreamUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.io.InputStream;

/**
 * Abstract class for command executors.
 */
public abstract class BaseCommandExecutor implements ICommandExecutor {

    protected Logger logger;
    private BaseCommandArguments baseArgs;
    private Runtime runtime;

    public BaseCommandExecutor(Logger logger, BaseCommandArguments baseArgs) {
        this.logger = logger;
        this.baseArgs = baseArgs;
        this.runtime = Runtime.getRuntime();
    }

    public boolean execute() {
        try {
            String execCommand = createExecutionCommand();

            // Run a java app in a separate system process
            logger.info("About to execute command: " + execCommand);

            Process proc = runtime.exec(execCommand);

            // give timeout for execution
            ProcessUtils processUtils = new ProcessUtils();
            int procExitValue = processUtils.waitFor(proc, baseArgs.getCommandExecutionTimeoutInSeconds());

            printStreams(proc);

            if (procExitValue == 0) {
                return true;
            }

        }catch (InterruptedException e){
            logger.error("Unable to perform '" + getCommandName() + "' command. The execution was too long and was interrupted. Error: ", e);
        } catch (Exception e) {
            logger.error("Unable to perform '" + getCommandName() + "' command. Error: ", e);
        }

        return false;
    }

    private void printStreams(Process proc) {
        // Receive the process output
        InputStream inputStream = proc.getInputStream();
        InputStream errorStream = proc.getErrorStream();
        String outputInfo = StreamUtils.toString(inputStream);
        String outputErrors = StreamUtils.toString(errorStream);
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

    protected String getCommandName() {
        return baseArgs.getMode().getCurrentMode().getName();
    }

    public abstract String getAdditionalArguments();

    private String getBaseArgumentsLine() {
        StringBuilder sb = new StringBuilder();

        if (baseArgs.getTokenData() != null) {
            addArgumentKeyVal(sb, "token", baseArgs.getTokenData().getToken());
        } else {
            addArgumentKeyVal(sb, "token", baseArgs.getToken());
            addArgumentKeyVal(sb, "tokenfile", baseArgs.getTokenFile());
            addArgumentKeyVal(sb, "customerid", baseArgs.getCustomerId());
            addArgumentKeyVal(sb, "server", baseArgs.getUrl());
        }

        addArgumentKeyVal(sb, "buildsessionid", baseArgs.getBuildSessionId());
        addArgumentKeyVal(sb, "buildsessionidfile", baseArgs.getBuildSessionIdFile());
        addArgumentKeyVal(sb, "appname", baseArgs.getAppName());
        addArgumentKeyVal(sb, "buildname", baseArgs.getBuildName());
        addArgumentKeyVal(sb, "branchname", baseArgs.getBranchName());

        addArgumentKeyVal(sb, "environment", baseArgs.getEnvironment());
        addArgumentKeyVal(sb, "proxy", baseArgs.getProxy());

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

    protected String resolvedJavaPath() {
        if (!StringUtils.isNullOrEmpty(baseArgs.getJavaPath()))
            return baseArgs.getJavaPath();

        return "java";
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }
}
