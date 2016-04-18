package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Installs tools selected by the user. Exports configured paths and a home variable for each tool.
 *
 * @author rcampbell
 * @author Oleg Nenashev
 *
 */
public class SeaLightsJenkinsBuildWrapper extends BuildWrapper {


    private final String koko;

    @DataBoundConstructor
    public SeaLightsJenkinsBuildWrapper(String koko) {
        this.koko = koko;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
                             BuildListener listener) throws IOException, InterruptedException {

        DESCRIPTOR.getCustonmerId()
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

    @Override
    public Descriptor<BuildWrapper> getDescriptor() {
        return DESCRIPTOR;
    }

    public String getKoko() {
        return koko;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        private String custonmerId;
        private String url;
        private boolean enable;

        @DataBoundConstructor
        public DescriptorImpl() {
            super(SeaLightsJenkinsBuildWrapper.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "chipopo lamoral!!";//Messages.Descriptor_DisplayName();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        public boolean isEnable(){
            return enable;
        }

        public String getCustonmerId() {
            return custonmerId;
        }

        public String getUrl() {
            return url;
        }
    }
}