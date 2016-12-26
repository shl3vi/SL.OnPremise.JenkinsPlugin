package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Created by shahar on 12/26/2016.
 */
public class ConfigCommandArguments {
    private BaseCommandArguments baseArgs;
    private String packagesIncluded;
    private String packagesExcluded;

    public ConfigCommandArguments(BaseCommandArguments baseArgs, String packagesIncluded, String packagesExcluded) {
        this.baseArgs = baseArgs;
        this.packagesIncluded = packagesIncluded;
        this.packagesExcluded = packagesExcluded;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
    }

    public String getPackagesIncluded() {
        return packagesIncluded;
    }

    public String getPackagesExcluded() {
        return packagesExcluded;
    }
}
