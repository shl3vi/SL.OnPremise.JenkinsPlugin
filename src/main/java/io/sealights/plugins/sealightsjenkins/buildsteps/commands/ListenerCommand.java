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
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandBuildNamingStrategy;
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
            // This step must be first
            setDefaultValues();

            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            EnvVars envVars = build.getEnvironment(listener);
            BaseCommandArguments baseArgs = createBaseCommandArguments(logger, build, additionalProps, envVars);
            baseArgs.setMode(commandMode);

            String filesStorage = resolveFilesStorage(additionalProps);

            ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandler(
                    logger,
                    filesStorage,
                    baseArgs
            );

            listenerCommandHandler.handle();

        } catch (Exception e) {
            logger.error("Error occurred while performing 'Sealights Listener Command'. Error: ", e);
        }

        return true;
    }

    private BaseCommandArguments createBaseCommandArguments(
            Logger logger, AbstractBuild<?, ?> build, Properties additionalProps, EnvVars envVars) {

        BaseCommandArguments baseArgs = new BaseCommandArguments();
        setGlobalConfiguration(baseArgs, additionalProps, envVars);
        setConfiguration(logger, build, envVars, baseArgs);

        baseArgs.setAgentPath((String) additionalProps.get("agentpath"));
        baseArgs.setJavaPath((String) additionalProps.get("javapath"));

        return baseArgs;
    }

    private String resolveFilesStorage(Properties additionalProps) {
        String filesStorage = (String) additionalProps.get("filesstorage");
        if (!StringUtils.isNullOrEmpty(filesStorage)){
            return filesStorage;
        }

        filesStorage = this.beginAnalysis.getDescriptor().getFilesStorage();
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return filesStorage;
        }

        return System.getProperty("java.io.tmpdir");
    }

    private void setGlobalConfiguration(BaseCommandArguments baseArgs, Properties additionalProps, EnvVars envVars) {

        String customer = (String) additionalProps.get("customerid");
        if (StringUtils.isNullOrEmpty(customer)) {
            customer = beginAnalysis.getDescriptor().getCustomerId();
        }
        baseArgs.setCustomerId(JenkinsUtils.tryGetEnvVariable(envVars, customer));

        String url = (String) additionalProps.get("server");
        if (StringUtils.isNullOrEmpty(url)) {
            url = beginAnalysis.getDescriptor().getUrl();
        }
        baseArgs.setUrl(JenkinsUtils.tryGetEnvVariable(envVars, url));

        String proxy = (String) additionalProps.get("proxy");
        if (StringUtils.isNullOrEmpty(proxy)) {
            proxy = beginAnalysis.getDescriptor().getProxy();
        }
        baseArgs.setProxy(JenkinsUtils.tryGetEnvVariable(envVars, proxy));
    }

    private void setConfiguration(Logger logger, AbstractBuild<?, ?> build, EnvVars envVars, BaseCommandArguments baseArgs) {
        baseArgs.setAppName(JenkinsUtils.tryGetEnvVariable(envVars, appName));
        baseArgs.setBuildName(getFinalBuildName(build, logger));
        baseArgs.setBranchName(JenkinsUtils.tryGetEnvVariable(envVars, branchName));
        baseArgs.setEnvironment(JenkinsUtils.tryGetEnvVariable(envVars, environment));
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