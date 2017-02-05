package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * available build statuses for SeaLights.
 */
public enum SealightsBuildStatus {

    SUCCESS("success") {},
    FAILURE("failure") {};

    private final String name;

    SealightsBuildStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
