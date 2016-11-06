package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;


import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandModes;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommonCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StreamUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.io.IOException;

public abstract class AbstractExecutor {

    protected Logger logger;
    protected String agentPath;
    protected CommonCommandArguments commonArguments;
    protected final CommandModes command;

    public AbstractExecutor(Logger logger, String agentPath, CommandModes command, CommonCommandArguments commonArguments) {
        this.logger = logger;
        this.agentPath = agentPath;
        this.command = command;
        this.commonArguments = commonArguments;
    }

    public boolean execute() throws IOException {
        String arguments = getCommonArgumentsLine() + getAdditionalArguments();
        String execCommand = "java -jar " + agentPath + " " + command.getName() + " " + arguments;

        // Run a java app in a separate system process
        logger.info("About to execute command: " + execCommand);
        Process proc = Runtime.getRuntime().exec(execCommand);

        // Receive the process output
        String outputInfo = StreamUtils.toString(proc.getInputStream());
        String outputErrors = StreamUtils.toString(proc.getErrorStream());
        logger.info("Process ended with exit code: " + proc.exitValue());
        if (!StringUtils.isNullOrEmpty(outputInfo)){
            logger.info("Process output:");
            logger.info(outputInfo);
        }
        if (!StringUtils.isNullOrEmpty(outputErrors)){
            logger.info("Process errors output:");
            logger.error(outputErrors);
        }



        return true;
    }

    public abstract String getAdditionalArguments();

    protected String getCommonArgumentsLine() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "customerid", commonArguments.getCustomerId());
        addArgumentKeyVal(sb, "appname", commonArguments.getAppName());
        addArgumentKeyVal(sb, "branchname", commonArguments.getBranchName());
        addArgumentKeyVal(sb, "environment", commonArguments.getEnvironment());
        addArgumentKeyVal(sb, "server", commonArguments.getUrl());
        addArgumentKeyVal(sb, "proxy", commonArguments.getProxy());
        addArgumentKeyVal(sb, "buildname", commonArguments.getBuildName());

        return sb.toString();
    }

    protected void addArgumentKeyVal(StringBuilder sb, String key, String val){
        if (StringUtils.isNullOrEmpty(val)){
            return;
        }
        sb.append("-");
        sb.append(key);
        sb.append(" ");
        sb.append("\"" + val + "\"");
        sb.append(" ");
    }
}
