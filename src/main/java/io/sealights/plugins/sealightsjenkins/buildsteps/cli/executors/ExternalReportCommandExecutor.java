package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.ExternalReportCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

import java.util.List;

/**
 * Executor for the 'externalReport' command.
 */
public class ExternalReportCommandExecutor extends AbstractCommandExecutor {

    private ExternalReportCommandArguments externalReportArguments;

    public ExternalReportCommandExecutor(
            Logger logger, BaseCommandArguments baseCommandArguments, ExternalReportCommandArguments externalReportArguments) {
        super(logger, baseCommandArguments);
        this.externalReportArguments = externalReportArguments;
    }

    @Override
    public void addAdditionalArguments(List<String> commandsList) {
        addArgumentKeyVal("report", JenkinsUtils.resolveEnvVarsInString(baseArgs.getEnvVars(), externalReportArguments.getReport()), commandsList);
    }

    @Override
    protected String getCommandName() {
        return "externalReport";
    }

}
