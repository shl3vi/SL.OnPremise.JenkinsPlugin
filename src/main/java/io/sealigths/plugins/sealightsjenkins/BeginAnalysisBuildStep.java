package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.sealigths.plugins.sealightsjenkins.exceptions.SeaLightsIllegalStateException;
import io.sealigths.plugins.sealightsjenkins.utils.CustomFile;
import io.sealigths.plugins.sealightsjenkins.utils.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by shahar on 5/3/2016.
 */
public class BeginAnalysisBuildStep extends Builder {

    public final boolean enableSeaLights;
    public final BeginAnalysis beginAnalysis;
    public final String pomPath;

    @DataBoundConstructor
    public BeginAnalysisBuildStep(boolean enableSeaLights, BeginAnalysis beginAnalysis, String pomPath)
            throws IOException {

        this.enableSeaLights = enableSeaLights;
        this.beginAnalysis = beginAnalysis;
        this.pomPath = pomPath;
    }

    public boolean isEnableSeaLights() {
        return enableSeaLights;
    }

    public BeginAnalysis getBeginAnalysis() {
        return beginAnalysis;
    }

    public String getPomPath() {
        return pomPath;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        if (!enableSeaLights)
            return true;

        Logger logger = new Logger(listener.getLogger());
        CleanupManager cleanupManager = new CleanupManager(logger);
        CustomFile customFile = new CustomFile(logger, cleanupManager, pomPath);
        customFile.copyToSlave();

        try {
            return beginAnalysis.perform(build, cleanupManager, logger, pomPath);
        } catch (SeaLightsIllegalStateException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "SeaLights Continuous Testing - Begin Analysis - DEPRECATED";
        }
    }

}
