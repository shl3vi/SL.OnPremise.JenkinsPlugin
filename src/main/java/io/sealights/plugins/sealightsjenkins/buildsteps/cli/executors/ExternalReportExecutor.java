package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.ExternalReportArguments;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Executor for the 'externalReport' command.
 */
public class ExternalReportExecutor extends AbstractCommandExecutor {

    private ExternalReportArguments externalReportArguments;

    public ExternalReportExecutor(
            Logger logger, BaseCommandArguments baseCommandArguments, ExternalReportArguments externalReportArguments) {
        super(logger, baseCommandArguments);
        this.externalReportArguments = externalReportArguments;
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "report", JenkinsUtils.resolveEnvVarsInString(baseArgs.getEnvVars(), externalReportArguments.getReport()));
        return sb.toString();
    }

    @Override
    protected String getCommandName() {
        return "externalReport";
    }

}
