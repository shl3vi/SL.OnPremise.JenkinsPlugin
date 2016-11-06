package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Created by shahar on 11/4/2016.
 */
public class EndCommandArguments extends AbstractCommandArguments{

    public EndCommandArguments(CommonCommandArguments commonCommandArguments) {
        super(CommandModes.End, commonCommandArguments);
    }
}
