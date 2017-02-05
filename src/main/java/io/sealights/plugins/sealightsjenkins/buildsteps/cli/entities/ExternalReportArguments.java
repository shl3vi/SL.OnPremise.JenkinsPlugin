package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Arguments for the 'externalReport' command.
 */
public class ExternalReportArguments {

    private BaseCommandArguments baseArgs;
    private String report;

    public ExternalReportArguments(BaseCommandArguments baseArgs, String report) {
        this.baseArgs = baseArgs;
        this.report = report;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
    }

    public String getReport() {
        return report;
    }
}
