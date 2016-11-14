package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Arguments for the 'end' command.
 */
public class EndCommandArguments{

    private BaseCommandArguments baseArgs;

    public EndCommandArguments(BaseCommandArguments baseArgs) {
        this.baseArgs = baseArgs;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
    }
}
