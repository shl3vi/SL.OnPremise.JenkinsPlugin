package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Created by shahar on 12/26/2016.
 */
public class ConfigCommandArguments extends AbstractCommandArgument {
    private String packagesIncluded;
    private String packagesExcluded;

    public ConfigCommandArguments(String packagesIncluded, String packagesExcluded) {
        this.packagesIncluded = packagesIncluded;
        this.packagesExcluded = packagesExcluded;
    }

    public String getPackagesIncluded() {
        return packagesIncluded;
    }

    public String getPackagesExcluded() {
        return packagesExcluded;
    }

    @Override
    public CommandModes getMode() {
        return CommandModes.Config;
    }
}
