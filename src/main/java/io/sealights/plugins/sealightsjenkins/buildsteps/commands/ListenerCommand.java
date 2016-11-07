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
import io.sealights.plugins.sealightsjenkins.BuildName;
import io.sealights.plugins.sealightsjenkins.BuildNamingStrategy;
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
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;

@ExportedBean
public class ListenerCommand extends Builder {

    private String appName;
    private String branchName;
    private BuildName buildName;
    private String environment;

    private String override_customerId;
    private String override_url;
    private String override_proxy;
    private BeginAnalysis beginAnalysis = new BeginAnalysis();

    @DataBoundConstructor
    public ListenerCommand(String appName, String branchName, BuildName buildName, String environment,
                           String override_customerId, String override_url, String override_proxy) {
        this.appName = appName;
        this.branchName = branchName;
        this.buildName = buildName;
        this.environment = environment;
        this.override_customerId = override_customerId;
        this.override_url = override_url;
        this.override_proxy = override_proxy;
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
    public BuildName getBuildName() {
        return buildName;
    }

    @Exported
    public void setBuildName(BuildName buildName) {
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
    public String getOverride_customerId() {
        return override_customerId;
    }

    @Exported
    public void setOverride_customerId(String override_customerId) {
        this.override_customerId = override_customerId;
    }

    @Exported
    public String getOverride_url() {
        return override_url;
    }

    @Exported
    public void setOverride_url(String override_url) {
        this.override_url = override_url;
    }

    @Exported
    public String getOverride_proxy() {
        return override_proxy;
    }

    @Exported
    public void setOverride_proxy(String override_proxy) {
        this.override_proxy = override_proxy;
    }

    @Exported
    public BeginAnalysis getBeginAnalysis() {
        return beginAnalysis;
    }

    @Exported
    public void setBeginAnalysis(BeginAnalysis beginAnalysis) {
        this.beginAnalysis = beginAnalysis;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return true;
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, CommandMode commandMode, Logger logger) throws IOException, InterruptedException {

        try {
            setDefaultValues(logger);
            EnvVars envVars = build.getEnvironment(listener);

            CommonCommandArguments commonArgs = new CommonCommandArguments();
            setGlobalConfiguration(commonArgs, envVars);

            commonArgs.setAppName(JenkinsUtils.tryGetEnvVariable(envVars, appName));
            commonArgs.setBuildName(getFinalBuildName(build, logger));
            commonArgs.setBranchName(JenkinsUtils.tryGetEnvVariable(envVars, branchName));
            commonArgs.setEnvironment(JenkinsUtils.tryGetEnvVariable(envVars, environment));

            AbstractUpgradeManager upgradeManager = createUpgradeManager(logger, commonArgs);
            String agentPath = upgradeManager.ensureLatestAgentPresentLocally();

            AbstractExecutor executor = executeCommand(logger, agentPath, commandMode, commonArgs);
            executor.execute();
        } catch (Exception e) {
            logger.error("Error occurred while performing 'Sealights Listener Command'. Error: ", e);
        }

        return true;
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

    private void setGlobalConfiguration(CommonCommandArguments commonArgs, EnvVars envVars) {

        String customer = override_customerId;
        if (StringUtils.isNullOrEmpty(customer)) {
            customer = beginAnalysis.getDescriptor().getCustomerId();
        }
        commonArgs.setCustomerId(JenkinsUtils.tryGetEnvVariable(envVars, customer));

        String url = override_url;
        if (StringUtils.isNullOrEmpty(url)) {
            url = beginAnalysis.getDescriptor().getUrl();
        }
        commonArgs.setUrl(url);

        String proxy = override_proxy;
        if (StringUtils.isNullOrEmpty(proxy)) {
            proxy = beginAnalysis.getDescriptor().getProxy();
        }
        commonArgs.setProxy(proxy);
    }

    private String getFinalBuildName(AbstractBuild<?, ?> build, Logger logger) throws IllegalStateException {

        String finalBuildName = null;

        if (BuildNamingStrategy.LATEST_BUILD.equals(buildName.getBuildNamingStrategy())) {
            return null;
        }

        if (BuildNamingStrategy.MANUAL.equals(buildName.getBuildNamingStrategy())) {
            finalBuildName = getManualBuildName();

        } else if (BuildNamingStrategy.JENKINS_UPSTREAM.equals(buildName.getBuildNamingStrategy())) {
            BuildName.UpstreamBuildName upstream = (BuildName.UpstreamBuildName) buildName;
            String upstreamProjectName = upstream.getUpstreamProjectName();
            finalBuildName = JenkinsUtils.getUpstreamBuildName(build, upstreamProjectName, logger);
        }

        if (StringUtils.isNullOrEmpty(finalBuildName)) {
            return String.valueOf(build.getNumber());
        }

        return finalBuildName;
    }

    private void setDefaultValues(Logger logger) {

        if (this.buildName == null)
            this.buildName = new BuildName.DefaultBuildName();
    }

    private String getManualBuildName() {
        BuildName.ManualBuildName manual = (BuildName.ManualBuildName) buildName;
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

        public DescriptorExtensionList<BuildName, BuildName.BuildNameDescriptor> getBuildNameDescriptorList() {
            return Jenkins.getInstance().getDescriptorList(BuildName.class);
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
