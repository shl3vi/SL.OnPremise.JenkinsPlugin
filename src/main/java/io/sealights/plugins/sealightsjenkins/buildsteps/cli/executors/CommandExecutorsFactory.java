package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.*;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * A factory to create command executors.
 */
public class CommandExecutorsFactory {

    public ICommandExecutor createExecutor(
            Logger logger, BaseCommandArguments baseArgs, AbstractCommandArgument commandArgument) {
        ICommandExecutor executor;

        if (baseArgs == null || commandArgument == null) {
            logger.error("BaseCommandArguments or commandArgument is 'null'. baseArgs: '" + baseArgs + "'");
            executor = new NullCommandExecutor();
        } else {
            if (CommandModes.Start.equals(commandArgument.getMode())) {
                executor = new StartCommandExecutor(logger, baseArgs, (StartCommandArguments) commandArgument);
            } else if (CommandModes.End.equals(commandArgument.getMode())) {
                executor = new EndCommandExecutor(logger, baseArgs, (EndCommandArguments) commandArgument);
            } else if (CommandModes.UploadReports.equals(commandArgument.getMode())) {
                executor = new UploadReportsCommandExecutor(logger, baseArgs, (UploadReportsCommandArguments) commandArgument);
            } else if (CommandModes.ExternalReport.equals(commandArgument.getMode())) {
                executor = new ExternalReportExecutor(logger, baseArgs, (ExternalReportArguments) commandArgument);
            } else if (CommandModes.Config.equals(commandArgument.getMode())) {
                executor = new ConfigCommandExecutor(logger, baseArgs, (ConfigCommandArguments) commandArgument);
            } else {
                logger.error("Current mode is invalid! Cannot create executor.");
                executor = new NullCommandExecutor();
            }
        }
        return executor;
    }

}
