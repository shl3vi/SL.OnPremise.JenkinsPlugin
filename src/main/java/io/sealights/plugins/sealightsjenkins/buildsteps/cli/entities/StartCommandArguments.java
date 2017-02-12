package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Arguments for the 'start' command.
 */
public class StartCommandArguments extends AbstractCommandArgument {

    private String newEnvironment;

    public StartCommandArguments(String newEnvironment) {
        this.newEnvironment = newEnvironment;
    }

    public String getNewEnvironment() {
        return newEnvironment;
    }

    @Override
    public CommandModes getMode() {
        return CommandModes.Start;
    }
}
