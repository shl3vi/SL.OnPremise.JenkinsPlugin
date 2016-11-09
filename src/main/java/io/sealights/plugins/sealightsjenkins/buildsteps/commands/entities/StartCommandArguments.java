package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Arguments for the 'start' command.
 */
public class StartCommandArguments {

    private BaseCommandArguments baseArgs;
    private String newEnvironment;

    public StartCommandArguments(BaseCommandArguments baseArgs, String newEnvironment) {
        this.baseArgs = baseArgs;
        this.newEnvironment = newEnvironment;
    }

    public String getNewEnvironment() {
        return newEnvironment;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
    }
}
