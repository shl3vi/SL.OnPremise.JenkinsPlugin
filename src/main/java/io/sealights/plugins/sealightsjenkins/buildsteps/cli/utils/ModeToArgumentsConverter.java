package io.sealights.plugins.sealightsjenkins.buildsteps.cli.utils;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.CommandMode;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.*;

/**
 * Created by shahar on 2/12/2017.
 */
public class ModeToArgumentsConverter {

    public AbstractCommandArgument convert(CommandMode mode){
        if (mode == null){
            return null;
        }

        if (CommandModes.Start.equals(mode.getCurrentMode())) {
            return toStartCommandArguments((CommandMode.StartView) mode);
        } else if (CommandModes.End.equals(mode.getCurrentMode())) {
            return toEndCommandArguments();
        } else if (CommandModes.UploadReports.equals(mode.getCurrentMode())) {
            return toUploadReportCommandArguments((CommandMode.UploadReportsView) mode);
        } else if (CommandModes.ExternalReport.equals(mode.getCurrentMode())) {
            return toExternalReportArguments((CommandMode.ExternalReportView) mode);
        } else if (CommandModes.Config.equals(mode.getCurrentMode())) {
            return toConfigCommandArguments((CommandMode.ConfigView) mode);
        }

        throw new IllegalStateException("toCommandArgument() - The provided CommandMode is not one of the expected types");
    }

    private StartCommandArguments toStartCommandArguments(CommandMode.StartView startView){
        return new StartCommandArguments(startView.getTestStage());
    }

    private EndCommandArguments toEndCommandArguments(){
        return new EndCommandArguments();
    }

    private UploadReportsCommandArguments toUploadReportCommandArguments(CommandMode.UploadReportsView uploadReportsView){
        return new UploadReportsCommandArguments(
                uploadReportsView.getReportFiles(),
                uploadReportsView.getReportsFolders(),
                uploadReportsView.getHasMoreRequests(),
                uploadReportsView.getSource());
    }

    private ExternalReportCommandArguments toExternalReportArguments(CommandMode.ExternalReportView externalReportView){
        return new ExternalReportCommandArguments(externalReportView.getReport());
    }

    private ConfigCommandArguments toConfigCommandArguments(CommandMode.ConfigView configView){
        return new ConfigCommandArguments(configView.getPackagesIncluded(), configView.getPackagesExcluded());
    }
}
