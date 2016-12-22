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
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.entities.ValidationError;
import io.sealights.plugins.sealightsjenkins.utils.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.List;
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

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener,
                           CommandMode commandMode, ListenerCommandHandler listenerCommandHandler, Logger logger)
            throws IOException, InterruptedException {

        try {
            // This step must be first
            setDefaultValues();

            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            EnvVars envVars = build.getEnvironment(listener);
            BaseCommandArguments baseArgs = createBaseCommandArguments(logger, build, additionalProps, envVars);
            baseArgs.setMode(commandMode);

            String filesStorage = resolveFilesStorage(additionalProps, envVars);

            if (baseArgs != null){
                logger.info("ListenerCommand.serverUrl:" + baseArgs.getUrl());
                logger.info("ListenerCommand.customerId:" + baseArgs.getCustomerId());
                if (baseArgs.getTokenData() != null){
                    logger.info("ListenerCommand.tokenData.serverUrl:" + baseArgs.getTokenData().getServer());
                    logger.info("ListenerCommand.tokenData.customerId:" + baseArgs.getTokenData().getCustomerId());
                    logger.info("ListenerCommand.tokenData.token: " + baseArgs.getTokenData().getToken());
                }
                else{
                    logger.warning("ListenerCommand.tokenData is null.");
                }
            }
            listenerCommandHandler.setBaseArgs(baseArgs);
            listenerCommandHandler.setFilesStorage(filesStorage);

            listenerCommandHandler.handle();

        } catch (Exception e) {
            logger.error("Error occurred while performing 'Sealights Listener Command'. Error: ", e);
        }

        return true;
    }

    private BaseCommandArguments createBaseCommandArguments(
            Logger logger, AbstractBuild<?, ?> build, Properties additionalProps, EnvVars envVars) {

        BaseCommandArguments baseArgs = new BaseCommandArguments();
        setGlobalConfiguration(logger, baseArgs, additionalProps, envVars);
        setConfiguration(logger, build, envVars, baseArgs);

        baseArgs.setAgentPath(resolveEnvVar(envVars, (String) additionalProps.get("agentpath")));
        baseArgs.setJavaPath(resolveEnvVar(envVars, (String) additionalProps.get("javapath")));

        return baseArgs;
    }

    private String resolveEnvVar(EnvVars envVars, String envVarKey) {
        return JenkinsUtils.tryGetEnvVariable(envVars, envVarKey);
    }

    private String resolveFilesStorage(Properties additionalProps, EnvVars envVars) {
        String filesStorage = (String) additionalProps.get("filesstorage");
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return resolveEnvVar(envVars, filesStorage);
        }

        filesStorage = this.beginAnalysis.getDescriptor().getFilesStorage();
        if (!StringUtils.isNullOrEmpty(filesStorage)) {
            return filesStorage;
        }

        return System.getProperty("java.io.tmpdir");
    }

    private void setGlobalConfiguration(Logger logger, BaseCommandArguments baseArgs, Properties additionalProps, EnvVars envVars) {

        String tokenPropertyValue = JenkinsUtils.tryGetEnvVariable(envVars, (String) additionalProps.get("token"));
        boolean usingToken = tryUseToken(logger, baseArgs, tokenPropertyValue);

        if (!usingToken) {
            String customer = (String) additionalProps.get("customerid");
            if (StringUtils.isNullOrEmpty(customer)) {
                customer = beginAnalysis.getDescriptor().getCustomerId();
            }
            baseArgs.setCustomerId(resolveEnvVar(envVars, customer));

            String url = (String) additionalProps.get("server");
            if (StringUtils.isNullOrEmpty(url)) {
                url = beginAnalysis.getDescriptor().getUrl();
            }
            baseArgs.setUrl(resolveEnvVar(envVars, url));
        }

        String proxy = (String) additionalProps.get("proxy");
        if (StringUtils.isNullOrEmpty(proxy)) {
            proxy = beginAnalysis.getDescriptor().getProxy();
        }
        baseArgs.setProxy(resolveEnvVar(envVars, proxy));
    }

    private boolean tryUseToken(
            Logger logger, BaseCommandArguments baseArgs, String tokenPropertyValue) {
        try {
            String token = tokenPropertyValue;
            if (StringUtils.isNullOrEmpty(token)) {
                token = beginAnalysis.getDescriptor().getToken();
                if (StringUtils.isNullOrEmpty(token)) {
                    logger.warning("Sealights token is not set. Sealights will try to run without it.");
                    return false;
                }
            }

            boolean isValidToken = validateAndTryUseToken(logger, token, baseArgs);
            if (!isValidToken) {
                logger.error("The provided token is invalid. Sealights will try to run without it.");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed to use token. Error: ", e);
            return false;
        }
    }

    private boolean validateAndTryUseToken(Logger logger, String token, BaseCommandArguments baseArgs) {
        TokenData tokenData;
        try {
            tokenData = TokenData.parse(token);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid token. Error: ", e);
            return false;
        }

        TokenValidator tokenValidator = new TokenValidator();
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);
        if (validationErrors.size() > 0) {
            logger.error("Invalid token. The token contains the following errors:");
            for (ValidationError validationError : validationErrors) {
                logger.error("Field: '" + validationError.getName() + "', Error: '" + validationError.getProblem() + "'.");
            }
            return false;
        }

        baseArgs.setTokenData(tokenData);
        return true;
    }

    private void setConfiguration(Logger logger, AbstractBuild<?, ?> build, EnvVars envVars, BaseCommandArguments baseArgs) {
        baseArgs.setAppName(resolveEnvVar(envVars, appName));
        baseArgs.setBuildName(getFinalBuildName(build, logger));
        baseArgs.setBranchName(resolveEnvVar(envVars, branchName));
        baseArgs.setEnvironment(resolveEnvVar(envVars, environment));
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
