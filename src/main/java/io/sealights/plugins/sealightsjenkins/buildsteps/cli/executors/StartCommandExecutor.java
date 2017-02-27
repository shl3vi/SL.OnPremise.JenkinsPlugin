package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.StartCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

import java.util.List;

/**
 * Executor for the 'start' command.
 */
public class StartCommandExecutor extends AbstractCommandExecutor {

    private StartCommandArguments startCommandArguments;

    public StartCommandExecutor(
            Logger logger, BaseCommandArguments baseCommandArguments, StartCommandArguments startCommandArguments) {
        super(logger, baseCommandArguments);
        this.startCommandArguments = startCommandArguments;
    }

    @Override
    public void addAdditionalArguments(List<String> commandsList) {
        addArgumentKeyVal("testStage", JenkinsUtils.resolveEnvVarsInString(baseArgs.getEnvVars(), startCommandArguments.getTestStage()), commandsList);
    }

    @Override
    protected String getCommandName() {
        return "start";
    }
}
