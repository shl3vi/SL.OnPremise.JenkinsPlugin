package io.sealigths.plugins.sealightsjenkins;

import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.mvn.GlobalMavenConfig;
import jenkins.mvn.GlobalSettingsProvider;
import jenkins.mvn.SettingsProvider;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shahar on 5/3/2016.
 */
public class BeforeAnalysisBuildStep extends Builder {

    public final SeaLightsJenkinsBuildWrapper slJenkinsBuildWrapper;

    @DataBoundConstructor
    public BeforeAnalysisBuildStep(SeaLightsJenkinsBuildWrapper slJenkinsBuildWrapper) {
        this.slJenkinsBuildWrapper = slJenkinsBuildWrapper;
    }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        slJenkinsBuildWrapper.setUp(build,launcher,listener);
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    public SeaLightsJenkinsBuildWrapper getSlJenkinsBuildWrapper() {
        return slJenkinsBuildWrapper;
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
            return "SeaLights Continuous Testing After Analysis";
        }

//        @Override
//        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
//            return req.bindJSON(MavenSealightsBuildStep.class,formData);
//        }
    }

}
