package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.EndCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Executor for the 'end' command.
 */
public class EndCommandExecutor extends BaseCommandExecutor {

    private EndCommandArguments endCommandArguments;

    public EndCommandExecutor(Logger logger, EndCommandArguments endCommandArguments) {
        super(logger, endCommandArguments.getBaseArgs());
        this.endCommandArguments = endCommandArguments;
    }

    @Override
    public String getAdditionalArguments() {
        return "";
    }

}
