package io.sealights.plugins.sealightsjenkins.buildsteps.commands;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors.CommandExecutorsFactory;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors.ICommandExecutor;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.AbstractUpgradeManager;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.TestListenerUpgradeManager;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.UpgradeProxy;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;

import java.io.File;

/**
 * This class is responsible to invoke the right command executor.
 */
public class ListenerCommandHandler {

    private Logger logger;
    private String filesStorage;
    private BaseCommandArguments baseArgs;

    public ListenerCommandHandler(Logger logger) {
        this.logger = logger;
    }

    public boolean handle(){
        String agentPath = tryGetAgentPath(logger, baseArgs);
        baseArgs.setAgentPath(agentPath);

        CommandExecutorsFactory commandExecutorsFactory = new CommandExecutorsFactory();
        ICommandExecutor executor = commandExecutorsFactory.createExecutor(logger, baseArgs);

        return executor.execute();
    }

    private String tryGetAgentPath(Logger logger, BaseCommandArguments baseArgs) {
        if (!StringUtils.isNullOrEmpty(baseArgs.getAgentPath()) && new File(baseArgs.getAgentPath()).isFile()) {
            return baseArgs.getAgentPath();
        }
        AbstractUpgradeManager upgradeManager = createUpgradeManager(logger, baseArgs);
        return upgradeManager.ensureLatestAgentPresentLocally();
    }

    private AbstractUpgradeManager createUpgradeManager(Logger logger, BaseCommandArguments baseArgs) {
        UpgradeConfiguration upgradeConfiguration = createUpgradeConfiguration(baseArgs);
        UpgradeProxy upgradeProxy = new UpgradeProxy(upgradeConfiguration, logger);
        return new TestListenerUpgradeManager(upgradeProxy, upgradeConfiguration, logger);
    }

    private UpgradeConfiguration createUpgradeConfiguration(BaseCommandArguments baseArgs) {

        return new UpgradeConfiguration(
                baseArgs.getCustomerId(),
                baseArgs.getAppName(),
                baseArgs.getEnvironment(),
                baseArgs.getBranchName(),
                baseArgs.getUrl(),
                baseArgs.getProxy(),
                filesStorage
        );
    }

    public String getFilesStorage() {
        return filesStorage;
    }

    public void setFilesStorage(String filesStorage) {
        this.filesStorage = filesStorage;
    }

    public BaseCommandArguments getBaseArgs() {
        return baseArgs;
    }

    public void setBaseArgs(BaseCommandArguments baseArgs) {
        this.baseArgs = baseArgs;
    }
}
