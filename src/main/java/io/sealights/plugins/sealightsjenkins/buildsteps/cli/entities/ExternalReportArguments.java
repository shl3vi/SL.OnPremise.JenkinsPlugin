package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * Arguments for the 'externalReport' command.
 */
public class ExternalReportArguments extends AbstractCommandArgument {

    private String report;

    public ExternalReportArguments(String report) {
        this.report = report;
    }

    public String getReport() {
        return report;
    }

    @Override
    public CommandModes getMode() {
        return CommandModes.ExternalReport;
    }
}
