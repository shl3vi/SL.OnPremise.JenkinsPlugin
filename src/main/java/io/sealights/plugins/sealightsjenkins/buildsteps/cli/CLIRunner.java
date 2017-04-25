package io.sealights.plugins.sealightsjenkins.buildsteps.cli;

import hudson.DescriptorExtensionList;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.sealights.plugins.sealightsjenkins.BeginAnalysis;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.AbstractCommandArgument;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.CommandBuildNamingStrategy;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.CommandModes;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.utils.BuildNameResolver;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.utils.ModeToArgumentsConverter;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.entities.ValidationError;
import io.sealights.plugins.sealightsjenkins.exceptions.SeaLightsIllegalStateException;
import io.sealights.plugins.sealightsjenkins.utils.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@ExportedBean
public class CLIRunner extends Builder {

    private String buildSessionId;
    private String appName;
    private String branchName;
    private CommandBuildName buildName;
    private String labId;
    private String additionalArguments;
    private BeginAnalysis beginAnalysis = new BeginAnalysis();

    @DataBoundConstructor
    public CLIRunner(String buildSessionId, String appName, String branchName,
                     CommandBuildName buildName, String labId, String additionalArguments) {
        this.buildSessionId = buildSessionId;
        this.appName = appName;
        this.branchName = branchName;
        this.buildName = buildName;
        this.labId = labId;
        this.additionalArguments = additionalArguments;
    }

    @Exported
    public String getBuildSessionId() {
        return buildSessionId;
    }

    @Exported
    public void setBuildSessionId(String buildSessionId) {
        this.buildSessionId = buildSessionId;
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
    public String getLabId() {
        return labId;
    }

    @Exported
    public void setLabId(String labId) {
        this.labId = labId;
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
                           CommandMode commandMode, CLIHandler cliHandler, Logger logger)
            throws IOException, InterruptedException {

        try {
            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);
            validateCommandMode(commandMode, additionalProps);

            // This step must be first
            setDefaultValues();


            EnvVars envVars = build.getEnvironment(listener);
            BaseCommandArguments baseArgs = createBaseCommandArguments(logger, build, additionalProps, envVars);

            baseArgs.setBuild(build);
            baseArgs.setEnvVars(envVars);
            baseArgs.setLogger(logger);

            String filesStorage = resolveFilesStorage(additionalProps, envVars);


            cliHandler.setBaseArgs(baseArgs);
            ModeToArgumentsConverter modeToArgumentsConverter = new ModeToArgumentsConverter();
            AbstractCommandArgument commandArguments = modeToArgumentsConverter.convert(commandMode);
            cliHandler.setCommandArgument(commandArguments);
            cliHandler.setFilesStorage(filesStorage);

            return cliHandler.handle();
        } catch (Exception e) {
            // for cases when property fields setup is invalid.
            if (e instanceof SeaLightsIllegalStateException) {
                throw e;
            }
            logger.error("Error occurred while performing 'Sealights CLI'. Error: ", e);
        }

        return false;
    }

    private void validateCommandMode(CommandMode commandMode, Properties additionalProps) {
        String buildsessionidfilePath = additionalProps.getProperty("buildsessionidfile");
        if (CommandModes.Config.equals(commandMode.getCurrentMode())) {
            validateConfigMode();
            return;
        }
        if (!StringUtils.isNullOrEmpty(buildSessionId)){
            return;
        }
        if (!StringUtils.isNullOrEmpty(buildsessionidfilePath)){
            return;
        }
        if (StringUtils.isNullOrEmpty(appName) || StringUtils.isNullOrEmpty(branchName) ||
                CommandBuildNamingStrategy.EMPTY_BUILD.equals(buildName.getBuildNamingStrategy()) ||
                CommandBuildNamingStrategy.LATEST_BUILD.equals(buildName.getBuildNamingStrategy())) {
            throw new SeaLightsIllegalStateException(
                    "'App Name', 'Branch Name' and 'Build Name' are mandatory when 'Build Session Id' is not provided");
        }
    }

    private void validateConfigMode() {
        if (StringUtils.isNullOrEmpty(appName) || StringUtils.isNullOrEmpty(branchName) ||
                CommandBuildNamingStrategy.EMPTY_BUILD.equals(buildName.getBuildNamingStrategy()) ||
                CommandBuildNamingStrategy.LATEST_BUILD.equals(buildName.getBuildNamingStrategy())) {
            throw new SeaLightsIllegalStateException(
                    "'App Name', 'Branch Name' and 'Build Name' are mandatory for the SeaLights 'config' command");
        }
    }

    private BaseCommandArguments createBaseCommandArguments(
            Logger logger, AbstractBuild<?, ?> build, Properties additionalProps, EnvVars envVars) {

        BaseCommandArguments baseArgs = new BaseCommandArguments();
        setGlobalConfiguration(logger, baseArgs, additionalProps, envVars);
        setConfiguration(logger, build, envVars, baseArgs, additionalProps);

        baseArgs.setAgentPath(resolveEnvVar(envVars, (String) additionalProps.get("agentpath")));
        baseArgs.setJavaPath(resolveEnvVar(envVars, (String) additionalProps.get("javapath")));

        return baseArgs;
    }

    private String resolveEnvVar(EnvVars envVars, String envVarKey) {
        return JenkinsUtils.resolveEnvVarsInString(envVars, envVarKey);
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

        String tokenPropertyValue = JenkinsUtils.resolveEnvVarsInString(envVars, (String) additionalProps.get("token"));
        String tokenFilePropertyFile = JenkinsUtils.resolveEnvVarsInString(envVars, (String) additionalProps.get("tokenfile"));
        ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();

        String token = argumentFileResolver.resolve(logger, tokenPropertyValue, tokenFilePropertyFile);
        boolean usingToken = tryUseToken(logger, baseArgs, token);

        if (!usingToken) {
            String customer = (String) additionalProps.get("customerid");
            if (StringUtils.isNullOrEmpty(customer)) {
                customer = beginAnalysis.getDescriptor().getCustomerId();
            }
            baseArgs.setCustomerId(resolveEnvVar(envVars, customer));

            String server = (String) additionalProps.get("server");
            if (StringUtils.isNullOrEmpty(server)) {
                server = beginAnalysis.getDescriptor().getUrl();
            }
            baseArgs.setUrl(resolveEnvVar(envVars, server));

            boolean noCustomerOrServer = StringUtils.isNullOrEmpty(customer) || StringUtils.isNullOrEmpty(server);
            if (noCustomerOrServer) {
                throw new RuntimeException(
                        "Invalid configuration. " +
                                "Should provide 'server url' and 'customer id' when token is not provided. " +
                                "'customerId': '" + customer + "', 'server': '" + server + "'");
            }
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
        baseArgs.setToken(token);
        return true;
    }

    private void setConfiguration(Logger logger, AbstractBuild<?, ?> build, EnvVars envVars,
                                  BaseCommandArguments baseArgs, Properties additionalProps) {

        String buildSession = resolveBuildSessionId(logger, additionalProps);
        baseArgs.setBuildSessionId(resolveEnvVar(envVars, buildSession));

        baseArgs.setAppName(resolveEnvVar(envVars, appName));

        BuildNameResolver buildNameResolver = new BuildNameResolver();
        baseArgs.setBuildName(buildNameResolver.getFinalBuildName(build, envVars, buildName, logger));

        baseArgs.setBranchName(resolveEnvVar(envVars, branchName));
        baseArgs.setLabId(resolveEnvVar(envVars, labId));
    }

    private String resolveBuildSessionId(Logger logger, Properties additionalProps) {
        ArgumentFileResolver argumentFileResolver = new ArgumentFileResolver();
        String buildSessionIdFile = (String) additionalProps.get("buildsessionidfile");
        return argumentFileResolver.resolve(logger, buildSessionId, buildSessionIdFile);
    }

    private void setDefaultValues() {

        if (this.buildName == null)
            this.buildName = new CommandBuildName.DefaultBuildName();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public DescriptorImpl() {
            super(CLIRunner.class);
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

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }

    }
}
