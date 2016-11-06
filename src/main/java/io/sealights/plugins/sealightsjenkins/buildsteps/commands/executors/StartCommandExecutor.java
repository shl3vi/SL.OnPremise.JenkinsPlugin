package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandModes;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.StartCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Created by shahar on 11/4/2016.
 */
public class StartCommandExecutor extends AbstractExecutor {

    private StartCommandArguments startCommandArguments;

    public StartCommandExecutor(Logger logger, String agentPath, StartCommandArguments startCommandArguments) {
        super(logger, agentPath, CommandModes.Start, startCommandArguments.getCommonCommandArguments());
        this.startCommandArguments = startCommandArguments;
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "testPhase", startCommandArguments.getNewEnvironment());
        return sb.toString();
    }
}
