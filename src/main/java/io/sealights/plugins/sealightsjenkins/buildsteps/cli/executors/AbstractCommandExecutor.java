package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;


import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.PathUtils;
import io.sealights.plugins.sealightsjenkins.utils.StreamUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for command executors.
 */
public abstract class AbstractCommandExecutor implements ICommandExecutor {

    protected Logger logger;
    protected BaseCommandArguments baseArgs;
    private Runtime runtime;

    public AbstractCommandExecutor(Logger logger, BaseCommandArguments baseArgs) {
        this.logger = logger;
        this.baseArgs = baseArgs;
        this.runtime = Runtime.getRuntime();
    }

    public boolean execute() {
        try {
            String[] execCommand = createExecutionCommand();

            // Run a java app in a separate system process
            logger.info("About to execute command: " + Arrays.toString(prettifyToken(execCommand)));

            Process process = runtime.exec(execCommand);
            process.waitFor();

            printStreams(process);

            if (process.exitValue() == 0) {
                return true;
            }

        } catch (Exception e) {
            logger.error("Unable to perform '" + getCommandName() + "' command. Error: ", e);
        }

        return false;
    }

    private void printStreams(Process process) {
        // Receive the process output
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        String outputInfo = StreamUtils.toString(inputStream);
        String outputErrors = StreamUtils.toString(errorStream);
        logger.info("Process ended with exit code: " + process.exitValue());
        if (!StringUtils.isNullOrEmpty(outputInfo)) {
            logger.info("Process output:");
            logger.info(outputInfo);
        }
        if (!StringUtils.isNullOrEmpty(outputErrors)) {
            logger.info("Process errors output:");
            logger.error(outputErrors);
        }
    }


    public String[] createExecutionCommand() {
        List<String> commands = new ArrayList<>();

        String javaPath = resolvedJavaPath();
        commands.add(javaPath);
        commands.add("-jar");
        commands.add(baseArgs.getAgentPath());
        commands.add(getCommandName());

        addBaseArgumentsLine(commands);
        addAdditionalArguments(commands);

        String[] commandsArray = new String[commands.size()];
        commandsArray = commands.toArray(commandsArray);
        return commandsArray;
    }

    protected abstract String getCommandName();

    public abstract void addAdditionalArguments(List<String> commands);

    protected void addBaseArgumentsLine(List<String> commandsList) {
        if (baseArgs.getTokenData() != null) {
            addArgumentKeyVal("token", baseArgs.getTokenData().getToken(), commandsList);
        } else {
            addArgumentKeyVal("token", baseArgs.getToken(), commandsList);
            addArgumentKeyVal("tokenfile", baseArgs.getTokenFile(), commandsList);
            addArgumentKeyVal("customerid", baseArgs.getCustomerId(), commandsList);
            addArgumentKeyVal("server", baseArgs.getUrl(), commandsList);
        }

        addArgumentKeyVal("buildsessionid", baseArgs.getBuildSessionId(), commandsList);
        addArgumentKeyVal("buildsessionidfile", baseArgs.getBuildSessionIdFile(), commandsList);
        addArgumentKeyVal("appname", baseArgs.getAppName(), commandsList);
        addArgumentKeyVal("buildname", baseArgs.getBuildName(), commandsList);
        addArgumentKeyVal("branchname", baseArgs.getBranchName(), commandsList);

        addArgumentKeyVal("labid", baseArgs.getLabId(), commandsList);
        addArgumentKeyVal("proxy", baseArgs.getProxy(), commandsList);
    }

    protected void addArgumentKeyVal(String key, String val, List<String> commandsList) {
        if (StringUtils.isNullOrEmpty(val)) {
            return;
        }
        commandsList.add("-" + key);
        commandsList.add(val);
    }

    protected String resolvedJavaPath() {
        if (!StringUtils.isNullOrEmpty(baseArgs.getJavaPath()))
            return baseArgs.getJavaPath();

        String localJava = PathUtils.join(System.getProperty("java.home"), "bin", "java");
        return localJava;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }
    public String[] prettifyToken(String[] commands){
        String[] commandsClone = commands.clone();
          for (int i =0;i<=commandsClone.length;i++){
            if (commandsClone[i].equals("-token")){
                commandsClone[i+1] = StringUtils.trimStart(commandsClone[i+1]);
                break;
            }
        }
           return commandsClone;
       }
}
