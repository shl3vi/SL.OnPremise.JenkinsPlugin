package io.sealights.plugins.sealightsjenkins.buildsteps.nodejs;

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
import io.sealights.plugins.sealightsjenkins.utils.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.util.Properties;

/**
 * Created by shahar on 11/14/2016.
 */
public class BasicNodeBuildStep extends Builder {

    private String customerId;
    private String server;
    private String proxy;

    private String appName;
    private String branchName;
    private BuildName buildName;
    private String environment;
    private String workspacepath;
    private String additionalArguments;
    private BeginAnalysis beginAnalysis = new BeginAnalysis();
    private String testsDirectory;

    @DataBoundConstructor
    public BasicNodeBuildStep(String appName, String branchName, BuildName buildName,
                              String environment, String workspacepath, String additionalArguments, String testsDirectory) {
        this.appName = appName;
        this.branchName = branchName;
        this.buildName = buildName;
        this.environment = environment;
        this.workspacepath = workspacepath;
        this.additionalArguments = additionalArguments;
        this.testsDirectory = testsDirectory;
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
    public String getWorkspacepath() {
        return workspacepath;
    }

    @Exported
    public void setWorkspacepath(String workspacepath) {
        this.workspacepath = workspacepath;
    }

    @Exported
    public String getAdditionalArguments() {
        return additionalArguments;
    }

    @Exported
    public void setAdditionalArguments(String additionalArguments) {
        this.additionalArguments = additionalArguments;
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
    public String getTestsDirectory() {
        return testsDirectory;
    }

    @Exported
    public void setTestsDirectory(String testsDirectory) {
        this.testsDirectory = testsDirectory;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        try {
            setDefaultValues();
            Logger logger = new Logger(listener.getLogger());
            Properties additionalProps = PropertiesUtils.toProperties(additionalArguments);

            EnvVars envVars = build.getEnvironment(listener);
            setGlobal(additionalProps);
            setConfiguration(envVars);
            String resolvedBuildName = getFinalBuildName(build, logger);

            printStepHeadline("install build agent", logger);
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("npm i sl-cia");
            printStreams(pr, logger);

            printStepHeadline("create  sealights.json", logger);
            String createSlJsonCommand = "node node_modules/sl-cia/lib/sl-config-cia --customerid " + customerId +
                    " --server " + server;
            pr = rt.exec(createSlJsonCommand);
            printStreams(pr, logger);

            printStepHeadline("run build agent", logger);
            String runBuildAgentCommand = "node node_modules/sl-cia/lib/sl-cia --workspacepath " + workspacepath +
                    " --appname " + appName + "--build " + resolvedBuildName + " --branch " + branchName
                    + " --technology nodejs --scm git";
            pr = rt.exec(runBuildAgentCommand);
            printStreams(pr, logger);

            printStepHeadline("install footprints agent, sealights cover(version of istanbul), test listener (mocha reporter)", logger);
            String installSlCoverMochaCommand = "npm i sl-node sl-node-cover sl-node-mocha";
            pr = rt.exec(installSlCoverMochaCommand);
            printStreams(pr, logger);

            printStepHeadline("create sl-node config", logger);
            String createSlNodeConfigCommand = "node node_modules/.bin/sl-config --customerid " + customerId
                    + " --server " + server + " --appname " + appName + " --env " + environment;
            pr = rt.exec(createSlNodeConfigCommand);
            printStreams(pr, logger);

            printStepHeadline("run tests with sealights agent 1", logger);
            String runTestsWithSlAgent_1 = "node node_modules/sl-node-cover/lib/cli.js cover";
            pr = rt.exec(runTestsWithSlAgent_1);
            printStreams(pr, logger);

            printStepHeadline("run tests with sealights agent 2", logger);
            String runTestsWithSlAgent_2 = "node_modules/mocha/bin/_mocha -- -R sl-node-mocha " + testsDirectory;
            pr = rt.exec(runTestsWithSlAgent_2);
            printStreams(pr, logger);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void setConfiguration(EnvVars envVars) {
        this.appName = JenkinsUtils.tryGetEnvVariable(envVars, appName);
        this.branchName = JenkinsUtils.tryGetEnvVariable(envVars, branchName);
        this.environment = JenkinsUtils.tryGetEnvVariable(envVars, environment);
        this.workspacepath = JenkinsUtils.tryGetEnvVariable(envVars, workspacepath);
        this.testsDirectory = JenkinsUtils.tryGetEnvVariable(envVars, testsDirectory);
    }

    private void printStepHeadline(String step, Logger logger){
        logger.info("\n");
        logger.info("\n");
        logger.info("NOW PERFORMING - " + step);
    }

    protected void printStreams(Process proc, Logger logger) {
        // Receive the process output
        String outputInfo = StreamUtils.toString(proc.getInputStream());
        String outputErrors = StreamUtils.toString(proc.getErrorStream());
        logger.info("Process ended with exit code: " + proc.exitValue());
        if (!StringUtils.isNullOrEmpty(outputInfo)) {
            logger.info("Process output:");
            logger.info(outputInfo);
        }
        if (!StringUtils.isNullOrEmpty(outputErrors)) {
            logger.info("Process errors output:");
            logger.error(outputErrors);
        }
    }

    private void setGlobal(Properties additionalProps) {
        customerId = beginAnalysis.getDescriptor().getCustomerId();
        server = beginAnalysis.getDescriptor().getUrl();
        proxy = beginAnalysis.getDescriptor().getProxy();

        customerId = (String) additionalProps.get("customerid");
        server = (String) additionalProps.get("server");
        proxy = (String) additionalProps.get("proxy");
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

    private void setDefaultValues() {

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
            super(BasicNodeBuildStep.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Basic Node Builder";
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
