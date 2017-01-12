package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.ExternalReportArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Executor for the 'externalReport' command.
 */
public class ExternalReportExecutor extends BaseCommandExecutor {

    private ExternalReportArguments externalReportArguments;

    public ExternalReportExecutor(
            Logger logger, ExternalReportArguments externalReportArguments) {
        super(logger, externalReportArguments.getBaseArgs());
        this.externalReportArguments = externalReportArguments;
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "report", externalReportArguments.getReport());
        return sb.toString();
    }

}
