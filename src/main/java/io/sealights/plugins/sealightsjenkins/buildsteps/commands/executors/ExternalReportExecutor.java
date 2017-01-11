package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.ExternalReportArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * Executor for the 'externalReport' command.
 */
public class ExternalReportExecutor extends BaseCommandExecutor {

    private ExternalReportArguments externalReportArguments;

    public ExternalReportExecutor(
            Logger logger, BaseCommandArguments baseArgs, ExternalReportArguments externalReportArguments) {
        super(logger, baseArgs);
        this.externalReportArguments = externalReportArguments;
    }

    @Override
    public String getAdditionalArguments() {
        StringBuilder sb = new StringBuilder();
        addArgumentKeyVal(sb, "report", externalReportArguments.getReport());
        return sb.toString();
    }

    @Override
    protected String getBaseArgumentsLine() {
        StringBuilder sb = new StringBuilder();

        addArgumentKeyVal(sb, "token", externalReportArguments.getToken());
        addArgumentKeyVal(sb, "tokenfile", externalReportArguments.getTokenFile());

        addArgumentKeyVal(sb, "buildsessionid", externalReportArguments.getBuildSessionId());
        addArgumentKeyVal(sb, "buildsessionidfile", externalReportArguments.getBuildSessionIdFile());

        addArgumentKeyVal(sb, "proxy", externalReportArguments.getProxy());

        addArgumentKeyVal(sb, "appname", externalReportArguments.getAppName());
        addArgumentKeyVal(sb, "branchname", externalReportArguments.getBranchName());
        addArgumentKeyVal(sb, "buildname", externalReportArguments.getBuildName());

        return sb.toString();
    }
}
