package io.sealights.plugins.sealightsjenkins.buildsteps.cli;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.sealights.plugins.sealightsjenkins.exceptions.SeaLightsIllegalStateException;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class SealightsCLIBuildStep extends Builder {

    public boolean enabled;
    public boolean failBuildIfStepFail;
    public CommandMode commandMode;
    public CLIRunner cliRunner;

    @DataBoundConstructor
    public SealightsCLIBuildStep(boolean enabled, boolean failBuildIfStepFail,
                                 CommandMode commandMode, CLIRunner cliRunner) {
        this.enabled = enabled;
        this.failBuildIfStepFail = failBuildIfStepFail;
        this.commandMode = commandMode;
        this.cliRunner = cliRunner;
    }

    public CommandMode getCommandMode() {
        return commandMode;
    }

    public void setCommandMode(CommandMode commandMode) {
        this.commandMode = commandMode;
    }

    public CLIRunner getCliRunner() {
        return cliRunner;
    }

    public void setCliRunner(CLIRunner cliRunner) {
        this.cliRunner = cliRunner;
    }

    public boolean isEnable() {
        return enabled;
    }

    public void setEnable(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFailBuildIfStepFail() {
        return failBuildIfStepFail;
    }

    public void setFailBuildIfStepFail(boolean failBuildIfStepFail) {
        this.failBuildIfStepFail = failBuildIfStepFail;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        boolean isStepSuccessful = false;
        Logger logger = new Logger(listener.getLogger(), "SeaLights CLI - " + commandMode.getCurrentMode().getName());

        try {
            if (!enabled) {
                logger.info("Sealights CLI step is disabled.");
                return true;
            }

            CLIHandler cliHandler = new CLIHandler(logger);
            isStepSuccessful = cliRunner.perform(build, launcher, listener, commandMode, cliHandler, logger);
        } catch (Exception e) {
            // for cases when property fields setup is invalid.
            if (e instanceof SeaLightsIllegalStateException) {
                throw e;
            }
            logger.error("Error occurred while performing 'Sealights CLI Build Step'. Error: ", e);
        }

        if (failBuildIfStepFail) {
            return isStepSuccessful;
        }

        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public DescriptorExtensionList<CommandMode, CommandMode.CommandModeDescriptor> getCommandModeDescriptorList() {
            DescriptorExtensionList<CommandMode, CommandMode.CommandModeDescriptor> descriptorList = Jenkins.getInstance().getDescriptorList(CommandMode.class);
            return descriptorList;
        }

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(SealightsCLIBuildStep.class, formData);
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return "Sealights CLI";
        }
    }
}
