package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandModes;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.EndCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Created by shahar on 11/4/2016.
 */
public class EndCommandExecutor extends AbstractExecutor {

    private EndCommandArguments endCommandArguments;

    public EndCommandExecutor(Logger logger, String agentPath, EndCommandArguments endCommandArguments) {
        super(logger, agentPath, CommandModes.End, endCommandArguments.getCommonCommandArguments());
        this.endCommandArguments = endCommandArguments;
    }

    @Override
    public String getAdditionalArguments() {
        return "";
    }

}
