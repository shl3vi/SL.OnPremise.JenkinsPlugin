package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Created by shahar on 11/4/2016.
 */
public class StartCommandArguments extends AbstractCommandArguments{

    private String newEnvironment;

    public StartCommandArguments(CommonCommandArguments commonCommandArguments, String newEnvironment) {
        super(CommandModes.Start, commonCommandArguments);
        this.newEnvironment = newEnvironment;
    }

    public String getNewEnvironment() {
        return newEnvironment;
    }

}
