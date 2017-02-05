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

/**
 * Created by shahar on 12/29/2016.
 */
public class  ScannerCommandsBuildStep extends Builder {

    public ScannerCommand scannerCommand;

    @DataBoundConstructor
    public ScannerCommandsBuildStep(ScannerCommand scannerCommand) {
        this.scannerCommand = scannerCommand;
    }

    public ScannerCommand getScannerCommand() {
        return scannerCommand;
    }

    public void setScannerCommand(ScannerCommand scannerCommand) {
        this.scannerCommand = scannerCommand;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        Logger logger = new Logger(listener.getLogger());

        try {
            ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandler(logger);
            scannerCommand.perform(build, launcher, listener, listenerCommandHandler, logger);
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
            return req.bindJSON(ScannerCommandsBuildStep.class, formData);
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return "Sealights Scanner Command";
        }
    }
}

