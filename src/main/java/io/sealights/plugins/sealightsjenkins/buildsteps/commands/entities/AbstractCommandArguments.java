package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Created by shahar on 11/6/2016.
 */
public class AbstractCommandArguments {

    private CommandModes mode;
    private CommonCommandArguments commonCommandArguments;

    public AbstractCommandArguments(CommandModes mode, CommonCommandArguments commonCommandArguments) {
        this.mode = mode;
        this.commonCommandArguments = commonCommandArguments;
    }

    public CommandModes getMode() {
        return mode;
    }

    public CommonCommandArguments getCommonCommandArguments() {
        return commonCommandArguments;
    }

}
