package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Arguments for the 'start' command.
 */
public class StartCommandArguments extends AbstractCommandArgument {

    private String testStage;

    public StartCommandArguments(String testStage) {
        this.testStage = testStage;
    }

    public String getTestStage() {
        return testStage;
    }

    @Override
    public CommandModes getMode() {
        return CommandModes.Start;
    }
}
