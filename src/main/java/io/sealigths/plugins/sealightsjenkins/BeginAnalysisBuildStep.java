package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;

/**
 * Created by shahar on 5/3/2016.
 */
public class BeginAnalysisBuildStep extends Builder {

    public final boolean enableSeaLights;
    public final BeginAnalysis beginAnalysis;

    @DataBoundConstructor
    public BeginAnalysisBuildStep(boolean enableSeaLights, BeginAnalysis beginAnalysis) throws IOException {

        this.enableSeaLights = enableSeaLights;
        this.beginAnalysis = beginAnalysis;
    }

    public boolean isEnableSeaLights() {
        return enableSeaLights;
    }

    public BeginAnalysis getBeginAnalysis() {
        return beginAnalysis;
    }


    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        if (!enableSeaLights)
            return true;

        return beginAnalysis.perform(build,launcher,listener);
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
            return "SeaLights Continuous Testing - Begin Analysis";
        }
    }

}
