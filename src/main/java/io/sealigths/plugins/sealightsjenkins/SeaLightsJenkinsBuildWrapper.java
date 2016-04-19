package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.management.DescriptorKey;
import java.io.IOException;

/**
 * Installs tools selected by the user. Exports configured paths and a home variable for each tool.
 *
 * @author rcampbell
 * @author Oleg Nenashev
 *
 */
public class SeaLightsJenkinsBuildWrapper extends BuildWrapper {

    @DataBoundConstructor
    public SeaLightsJenkinsBuildWrapper() {
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
                             BuildListener listener) throws IOException, InterruptedException {

//        final EnvVars buildEnv = build.getEnvironment(listener);
//        final Node node = build.getBuiltOn();
//
//        return new Environment() {
//            @Override
//            public void buildEnvVars(Map<String, String> env) {
//
//                // TODO: Inject Home dirs as well
//                for (SelectedTool selectedTool : selectedTools) {
//                    CustomTool tool = selectedTool.toCustomTool();
//                    if (tool != null && tool.hasVersions()) {
//                        ToolVersion version = ToolVersion.getEffectiveToolVersion(tool, buildEnv, node);
//                        if (version != null && !env.containsKey(version.getVariableName())) {
//                            env.put(version.getVariableName(), version.getDefaultVersion());
//                        }
//                    }
//                }
//            }
//        };

        return null;
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


    //    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        private String customerId;
        private String url;
        private String proxy;
        private boolean enable;

        public DescriptorImpl() {
            super(SeaLightsJenkinsBuildWrapper.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Sealights properties";//Messages.Descriptor_DisplayName();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            enable = json.getBoolean("enable");
            customerId = json.getString("customerId");
            url = json.getString("url");
            proxy = json.getString("proxy");
            save();
            return super.configure(req, json);
        }


        public boolean isEnable(){
            return enable;
        }


        public String getUrl() {
            return url;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }
    }
}