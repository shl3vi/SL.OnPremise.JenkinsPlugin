package io.sealights.plugins.sealightsjenkins.buildsteps.commands;

import hudson.DescriptorExtensionList;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.sealights.plugins.sealightsjenkins.BeginAnalysis;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.*;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors.AbstractExecutor;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors.EndCommandExecutor;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors.StartCommandExecutor;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors.UploadReportsCommandExecutor;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.AbstractUpgradeManager;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.TestListenerUpgradeManager;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.UpgradeProxy;
import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.UpgradeConfiguration;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.PropertiesUtils;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@ExportedBean
public class ListenerCommand extends Builder {

    private String appName;
    private String branchName;
    private CommandBuildName buildName;
    private String environment;
    private String additionalArguments;
    private BeginAnalysis beginAnalysis = new BeginAnalysis();

    @DataBoundConstructor
    public ListenerCommand(String appName, String branchName, CommandBuildName buildName, String environment, String additionalArguments) {
        this.appName = appName;
        this.branchName = branchName;
        this.buildName = buildName;
        this.environment = environment;
        this.additionalArguments = additionalArguments;
    }


    @Exported
    public String getAppName() {
        return appName;
    }

    @Exported
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Exported
    public String getBranchName() {
        return branchName;
    }

    @Exported
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Exported
    public CommandBuildName getBuildName() {
        return buildName;
    }

    @Exported
    public void setBuildName(CommandBuildName buildName) {
        this.buildName = buildName;
    }

    @Exported
    public String getEnvironment() {
        return environment;
    }

    @Exported
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Exported
    public BeginAnalysis getBeginAnalysis() {
        return beginAnalysis;
    }

    @Exported
    public void setBeginAnalysis(BeginAnalysis beginAnalysis) {
        this.beginAnalysis = beginAnalysis;
    }

    @Exported
    public String getAdditionalArguments() {
        return additionalArguments;
    }

    @Exported
    public void setAdditionalArguments(String additionalArguments) {
        this.additionalArguments = additionalArguments;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return true;
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, CommandMode commandMode, Logger logger) throws IOException, InterruptedException {

        try {
            setDefaultValues();
            EnvVars envVars = build.getEnvironment(listener);

            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            CommonCommandArguments commonArgs = new CommonCommandArguments();
            setGlobalConfiguration(commonArgs, additionalProps, envVars);

            commonArgs.setAppName(JenkinsUtils.tryGetEnvVariable(envVars, appName));
            commonArgs.setBuildName(getFinalBuildName(build, logger));
            commonArgs.setBranchName(JenkinsUtils.tryGetEnvVariable(envVars, branchName));
            commonArgs.setEnvironment(JenkinsUtils.tryGetEnvVariable(envVars, environment));

            String agentPath = tryGetAgentPath(logger, commonArgs, additionalProps);
            AbstractExecutor executor = executeCommand(logger, agentPath, commandMode, commonArgs);
            executor.execute();

        } catch (Exception e) {
            logger.error("Error occurred while performing 'Sealights Listener Command'. Error: ", e);
        }

        return true;
    }

    private String tryGetAgentPath(Logger logger, CommonCommandArguments commonArgs, Properties props) {
        String agentPath = (String) props.get("agentpath");
        if (StringUtils.isNullOrEmpty(agentPath) || !(new File(agentPath).isFile())) {
            AbstractUpgradeManager upgradeManager = createUpgradeManager(logger, commonArgs);
            agentPath = upgradeManager.ensureLatestAgentPresentLocally();
        }

        return agentPath;
    }

    private AbstractExecutor executeCommand(Logger logger, String agentPath, CommandMode commandMode, CommonCommandArguments commonArgs) {
        AbstractExecutor executor;

        if (CommandModes.Start.equals(commandMode.getCurrentMode())) {
            StartCommandArguments startCommandArguments = getStartCommandArguments(commandMode, commonArgs);
            executor = new StartCommandExecutor(logger, agentPath, startCommandArguments);
        } else if (CommandModes.End.equals(commandMode.getCurrentMode())) {
            EndCommandArguments endCommandArguments = getEndCommandArguments(commonArgs);
            executor = new EndCommandExecutor(logger, agentPath, endCommandArguments);
        } else {

            UploadReportsCommandArguments uploadReportsCommandArguments = getUploadReportsCommandArguments(commandMode, commonArgs);
            executor = new UploadReportsCommandExecutor(logger, agentPath, uploadReportsCommandArguments);
        }

        return executor;
    }

    private StartCommandArguments getStartCommandArguments(CommandMode commandMode, CommonCommandArguments commonArgs) {
        CommandMode.StartView startView = (CommandMode.StartView) commandMode;
        return new StartCommandArguments(commonArgs, startView.getNewEnvironment());
    }

    private EndCommandArguments getEndCommandArguments(CommonCommandArguments commonArgs) {
        return new EndCommandArguments(commonArgs);
    }

    private UploadReportsCommandArguments getUploadReportsCommandArguments(CommandMode commandMode, CommonCommandArguments commonArgs) {
        CommandMode.UploadReportsView uploadReportsView = (CommandMode.UploadReportsView) commandMode;
        return new UploadReportsCommandArguments(
                commonArgs,
                uploadReportsView.getReportFiles(),
                uploadReportsView.getReportsFolders(),
                uploadReportsView.getHasMoreRequests(),
                uploadReportsView.getSource());
    }

    private AbstractUpgradeManager createUpgradeManager(Logger logger, CommonCommandArguments commonArgs) {
        UpgradeConfiguration upgradeConfiguration = createUpgradeConfiguration(commonArgs);
        UpgradeProxy upgradeProxy = new UpgradeProxy(upgradeConfiguration, logger);
        return new TestListenerUpgradeManager(upgradeProxy, upgradeConfiguration, logger);
    }

    private UpgradeConfiguration createUpgradeConfiguration(CommonCommandArguments commonArgs) {
        String filesStorage = this.beginAnalysis.getDescriptor().getFilesStorage();
        if (StringUtils.isNullOrEmpty(filesStorage)) {
            filesStorage = System.getProperty("java.io.tmpdir");
        }

        return new UpgradeConfiguration(
                commonArgs.getCustomerId(),
                commonArgs.getAppName(),
                commonArgs.getEnvironment(),
                commonArgs.getBranchName(),
                commonArgs.getUrl(),
                commonArgs.getProxy(),
                filesStorage
        );
    }


    private void setGlobalConfiguration(CommonCommandArguments commonArgs, Properties additionalProps, EnvVars envVars) {

        String customer = (String) additionalProps.get("customerid");
        if (StringUtils.isNullOrEmpty(customer)) {
            customer = beginAnalysis.getDescriptor().getCustomerId();
        }
        commonArgs.setCustomerId(JenkinsUtils.tryGetEnvVariable(envVars, customer));

        String url = (String) additionalProps.get("server");
        if (StringUtils.isNullOrEmpty(url)) {
            url = beginAnalysis.getDescriptor().getUrl();
        }
        commonArgs.setUrl(JenkinsUtils.tryGetEnvVariable(envVars, url));

        String proxy = (String) additionalProps.get("proxy");
        if (StringUtils.isNullOrEmpty(proxy)) {
            proxy = beginAnalysis.getDescriptor().getProxy();
        }
        commonArgs.setProxy(JenkinsUtils.tryGetEnvVariable(envVars, proxy));
    }

    private String getFinalBuildName(AbstractBuild<?, ?> build, Logger logger) throws IllegalStateException {

        String finalBuildName = null;

        if (CommandBuildNamingStrategy.LATEST_BUILD.equals(buildName.getBuildNamingStrategy())) {
            return null;
        }

        if (CommandBuildNamingStrategy.MANUAL.equals(buildName.getBuildNamingStrategy())) {
            finalBuildName = getManualBuildName();

        } else if (CommandBuildNamingStrategy.JENKINS_UPSTREAM.equals(buildName.getBuildNamingStrategy())) {
            CommandBuildName.UpstreamBuildName upstream = (CommandBuildName.UpstreamBuildName) buildName;
            String upstreamProjectName = upstream.getUpstreamProjectName();
            finalBuildName = JenkinsUtils.getUpstreamBuildName(build, upstreamProjectName, logger);
        }

        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            return String.valueOf(build.getNumber());
        }

        return finalBuildName;
    }

    private void setDefaultValues() {

        if (this.buildName == null)
            this.buildName = new CommandBuildName.DefaultBuildName();
    }

    private String getManualBuildName() {
        CommandBuildName.ManualBuildName manual = (CommandBuildName.ManualBuildName) buildName;
        String insertedBuildName = manual.getInsertedBuildName();
        return insertedBuildName;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public DescriptorImpl() {
            super(ListenerCommand.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "";
        }

        @Override
        public synchronized void load() {
            super.load();
        }

        public DescriptorExtensionList<CommandBuildName, CommandBuildName.CommandBuildNameDescriptor> getBuildNameDescriptorList() {
            return Jenkins.getInstance().getDescriptorList(CommandBuildName.class);
        }

        public FormValidation doCheckAppName(@QueryParameter String appName) {
            if (StringUtils.isNullOrEmpty(appName))
                return FormValidation.error("App Name is mandatory.");
            return FormValidation.ok();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }

    }
}
