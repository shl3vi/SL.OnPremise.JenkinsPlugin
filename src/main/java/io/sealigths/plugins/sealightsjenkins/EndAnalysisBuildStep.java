package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by shahar on 5/3/2016.
 */
public class EndAnalysisBuildStep extends Builder{

    public final RestoreBuildFile restoreBuildFile;

    @DataBoundConstructor
    public EndAnalysisBuildStep(RestoreBuildFile restoreBuildFile) {
        this.restoreBuildFile = restoreBuildFile;
    }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        restoreBuildFile.perform(build,launcher,listener);
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    public RestoreBuildFile getRestoreBuildFile() {
        return restoreBuildFile;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return "SeaLights Continuous Testing - End Analysis";
        }
        
    }
}
