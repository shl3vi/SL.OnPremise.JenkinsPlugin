package io.sealigths.plugins.sealightsjenkins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;
import io.sealigths.plugins.sealightsjenkins.integration.JarsHelper;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegration;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealigths.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Created by shahar on 4/25/2016.
 */
public class TechIntegration extends BuildWrapper {

    private final String pomPath;
    private final String relativePathToEffectivePom;


    @DataBoundConstructor
    public TechIntegration(String pomPath, String relativePathToEffectivePom) throws IOException {

        this.pomPath = pomPath;
        this.relativePathToEffectivePom = relativePathToEffectivePom;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
                             BuildListener listener) throws IOException, InterruptedException {

        listener.getLogger().println("-----------Sealights Jenkins Plugin Configuration--------------");
        listener.getLogger().println("pomPath:" +pomPath);
        listener.getLogger().println("relativePathToEffectivePom: " + relativePathToEffectivePom);
        listener.getLogger().println("-----------Sealights Jenkins Plugin Configuration--------------");

        Environment env = new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
            }
        };

        return env;
    }

    public DescriptorImpl getDescriptor() {
        Jenkins jenkinsInstance = Jenkins.getInstance();
        if (jenkinsInstance != null){
            Descriptor desc = jenkinsInstance.getDescriptorOrDie(getClass());
            if (desc != null){
                return (DescriptorImpl)desc;
            }
        }
        return new DescriptorImpl();
    }


    public String getPomPath() {
        return pomPath;
    }

    public String getRelativePathToEffectivePom() {
        return relativePathToEffectivePom;
    }

    private boolean isNullOrEmpty(String str)
    {
        return  (str == null || str.equals(""));
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(TechIntegration.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "The tech integration";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }


    }

}
