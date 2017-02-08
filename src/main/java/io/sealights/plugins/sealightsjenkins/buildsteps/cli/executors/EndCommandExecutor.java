package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.EndCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Executor for the 'end' command.
 */
public class EndCommandExecutor extends AbstractCommandExecutor {

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
