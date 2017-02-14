package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Arguments for the 'end' command.
 */
public class EndCommandArguments extends AbstractCommandArgument {

    @Override
    public CommandModes getMode() {
        return CommandModes.End;
    }
}
