package io.sealights.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by shahar on 1/9/2017.
 */
public class BuildStatusNotifier extends Notifier {

    private boolean overrideLatestBuild;

    @DataBoundConstructor
    public BuildStatusNotifier(final boolean overrideLatestBuild) {
        super();
        this.overrideLatestBuild = overrideLatestBuild;
    }

    public boolean getOverrideLatestBuild() {
        return this.overrideLatestBuild;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        Logger logger = new Logger(listener.getLogger(), "[SeaLights Build Status Notifier] ");

        logger.info("About to report build status.");
        try {
            reportBuildStatus();
        } catch (Exception e) {
            logger.error("");
        }

        logger.info("Report was successfully sent.");

        return true;
    }

    private void reportBuildStatus() {

    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        //This is here to ensure that the reported build status is actually correct. If we were to return false here,
        //other build plugins could still modify the build result, making the sent out HipChat notification incorrect.
        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "SeaLights - Report Build Status";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

    }
}