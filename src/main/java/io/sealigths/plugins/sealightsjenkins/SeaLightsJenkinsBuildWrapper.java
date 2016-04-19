package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.management.DescriptorKey;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Installs tools selected by the user. Exports configured paths and a home variable for each tool.
 *
 * @author rcampbell
 * @author Oleg Nenashev
 *
 */
public class SeaLightsJenkinsBuildWrapper extends BuildWrapper {


    private final boolean enable;
    private final String projectName;
    private final String projectType;
    private final String pomPath;
    private final String packagesincluded;
    private final String packagesexcluded;
    private final String filesincluded;
    private final String filesexcluded;
    private final String buildScannerJar;
    private final String testListenerJar;
    private final String testListenerConfigFile;
    private final String logEnabled;
    private final String logToFile;
    private final String logLevel;
    private final String logFolder;


    @DataBoundConstructor
    public SeaLightsJenkinsBuildWrapper(boolean enable, String projectName, String projectType, String pomPath,
                                        String packagesincluded, String packagesexcluded, String filesincluded,
                                        String filesexcluded, String buildScannerJar, String testListenerJar,
                                        String testListenerConfigFile, String logEnabled, String logToFile,
                                        String logLevel, String logFolder) {
        this.enable = enable;
        this.projectName = projectName;
        this.projectType = projectType;
        this.pomPath = pomPath;
        this.packagesincluded = packagesincluded;
        this.packagesexcluded = packagesexcluded;
        this.filesincluded = filesincluded;
        this.filesexcluded = filesexcluded;
        this.buildScannerJar = buildScannerJar;
        this.testListenerJar = testListenerJar;
        this.testListenerConfigFile = testListenerConfigFile;
        this.logEnabled = logEnabled;
        this.logToFile = logToFile;
        this.logLevel = logLevel;
        this.logFolder = logFolder;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
                             BuildListener listener) throws IOException, InterruptedException {

        listener.getLogger().println("build number: ");
        listener.getLogger().println(build.getNumber());
        listener.getLogger().println("branch: ");
        listener.getLogger().println();
        listener.getLogger().println("app display name: ");
        listener.getLogger().println(build.getDisplayName());
        listener.getLogger().println("app full display name: ");
        listener.getLogger().println(build.getFullDisplayName());
        listener.getLogger().println("working directory: ");

        FilePath ws = build.getWorkspace();
        if (ws != null) {
            String workingDir = ws.getRemote();
            listener.getLogger().println(workingDir);
            workingDir = workingDir + "\\java-build-agent\\src\\main\\java\\io\\sealights\\javaBuildAgent\\execute";
            listener.getLogger().println(workingDir);
            File file = new File(workingDir);
            if (file.isDirectory()){
                File[] children = file.listFiles();
                if (children != null){
                    listener.getLogger().println(":::::::::::::::::::::::::::::::::::::::::::::");
                    listener.getLogger().println(":::::::::::::::::::::::::::::::::::::::::::::");
                    for (File child : children){
                        listener.getLogger().println(child.getAbsolutePath());
                    }
                    listener.getLogger().println(":::::::::::::::::::::::::::::::::::::::::::::");
                    listener.getLogger().println(":::::::::::::::::::::::::::::::::::::::::::::");
                }
            }
        }
        listener.getLogger().println("customerid: ");
        listener.getLogger().println(getDescriptor().getCustomerId());

        listener.getLogger().println("url: ");
        listener.getLogger().println(getDescriptor().getUrl());

        listener.getLogger().println("enable: ");
        listener.getLogger().println(enable);
        listener.getLogger().println("project name: ");
        listener.getLogger().println(projectName);
        listener.getLogger().println("project type: ");
        listener.getLogger().println(projectType);

//        final EnvVars buildEnv = build.getEnvironment(listener);
//        final Node node = build.getBuiltOn();
//
        return new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
            }

        };
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

        public ListBoxModel doFillProjectTypesItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Maven","");
            items.add("Gradle");
            items.add("Ant");
            items.add("Ruby");
            items.add("NodeJs");
            return items;
        }
    }
}