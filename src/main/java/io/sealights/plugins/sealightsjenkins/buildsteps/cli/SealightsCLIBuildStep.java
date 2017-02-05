package io.sealights.plugins.sealightsjenkins.buildsteps.cli;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class SealightsCLIBuildStep extends Builder {

    public CommandMode commandMode;
    public CLIRunner cLIRunner;

    @DataBoundConstructor
    public SealightsCLIBuildStep(CommandMode commandMode, CLIRunner cLIRunner) {
        this.commandMode = commandMode;
        this.cLIRunner = cLIRunner;
    }

    public CommandMode getCommandMode() {
        return commandMode;
    }

    public void setCommandMode(CommandMode commandMode) {
        this.commandMode = commandMode;
    }

    public CLIRunner getcLIRunner() {
        return cLIRunner;
    }

    public void setcLIRunner(CLIRunner cLIRunner) {
        this.cLIRunner = cLIRunner;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        Logger logger = new Logger(listener.getLogger());

        try {
            CLIHandler cliHandler = new CLIHandler(logger);
            cLIRunner.perform(build, launcher, listener, commandMode, cliHandler, logger);
        }catch (Exception e){
            logger.error("Error occurred while performing 'Sealights Listener Command'. Error: ", e);
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
