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
import io.sealigths.plugins.sealightsjenkins.integration.JarsHelper;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegration;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealigths.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Map;

public class SeaLightsJenkinsBuildWrapper extends BuildWrapper {


    //private final boolean enable;
    private final String appName;
    private final String moduleName;
    private final String branch;
    //private final String projectType;
    private final String pomPath;
    private final String packagesincluded;
    //private final String packagesexcluded;
    private final String filesincluded;
    private final String filesexcluded;
    //private final String testingFramework;
    private final String relativePathToEffectivePom;
    private final boolean recursive;
    private final String workspacepath;
    private final String buildScannerJar;
    private final String testListenerJar;
    private final String apiJar;
    //private final String testListenerConfigFile;
    private final boolean inheritedBuild;
    private final boolean logEnabled;
    private final String logLevel;
    private final boolean logToFile;
    private final String logFolder;


    @DataBoundConstructor
    public SeaLightsJenkinsBuildWrapper(String appName, String moduleName, String branch, String pomPath,
                                        String packagesincluded, String filesincluded, String filesexcluded,
                                        String relativePathToEffectivePom, boolean recursive,
                                        String workspacepath, String buildScannerJar, String testListenerJar,
                                        boolean inheritedBuild, boolean logEnabled,
                                        String logLevel, boolean logToFile, String logFolder, String apiJar) throws IOException {
        //this.enable = enable;
        this.appName = appName;
        this.moduleName = moduleName;
        this.branch = branch;
        //this.projectType = projectType;
        this.pomPath = pomPath;
        this.packagesincluded = packagesincluded;
//        this.packagesexcluded = packagesexcluded;
        this.filesincluded = filesincluded;
        this.filesexcluded = filesexcluded;
        //this.testingFramework = testingFramework;
        this.relativePathToEffectivePom = relativePathToEffectivePom;
        this.recursive = recursive;
        this.workspacepath = workspacepath;
      //  this.testListenerConfigFile = testListenerConfigFile;
        this.inheritedBuild = inheritedBuild;
        this.logEnabled = logEnabled;
        this.logLevel = logLevel;
        this.logToFile = logToFile;
        this.logFolder = logFolder;

        if (isNullOrEmpty(buildScannerJar))
        {
            //The user didn't specify a specify version of the scanner. Use an embedded one.
            buildScannerJar = JarsHelper.loadJarAndSaveAsTempFile("sl-build-scanner");
        }

        if (isNullOrEmpty(testListenerJar))
        {
            //The user didn't specify a specify version of the test listener. Use an embedded one.
            testListenerJar = JarsHelper.loadJarAndSaveAsTempFile("sl-test-listener");
        }

        if (isNullOrEmpty(apiJar))
        {
            //The user didn't specify a specify version of the test listener. Use an embedded one.
            apiJar = JarsHelper.loadJarAndSaveAsTempFile("sl-api");
        }


        this.buildScannerJar = buildScannerJar;
        this.testListenerJar = testListenerJar;
        this.apiJar = apiJar;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
                             BuildListener listener) throws IOException, InterruptedException {

        listener.getLogger().println("**************************************************");
        //listener.getLogger().println("testingFramework:" +testingFramework);
        //listener.getLogger().println("enable:" +enable);
        listener.getLogger().println("appName:" +appName);
        listener.getLogger().println("moduleName:" +moduleName);
        //listener.getLogger().println("projectType:" +projectType);
        listener.getLogger().println("pomPath:" +pomPath);
        listener.getLogger().println("packagesincluded:" +packagesincluded);
        //listener.getLogger().println("packagesexcluded:" +packagesexcluded);
        listener.getLogger().println("filesincluded:" +filesincluded);
        listener.getLogger().println("filesexcluded:" +filesexcluded);
        listener.getLogger().println("buildScannerJar:" +buildScannerJar);
        listener.getLogger().println("testListenerJar:" +testListenerJar);
        listener.getLogger().println("apiJar:" +apiJar);
        listener.getLogger().println("LogsEnabled:" + logEnabled);
        listener.getLogger().println("logToFile:" +logToFile);
        listener.getLogger().println("logLevel:" +logLevel);
        listener.getLogger().println("logFolder:" +logFolder);

        Environment env = new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
            }
        };

        FilePath ws = build.getWorkspace();
        if (ws == null) {
            return env;
        }

        String workingDir = ws.getRemote();
        String pomPath;
        if (relativePathToEffectivePom != null && !"".equals(relativePathToEffectivePom))
            pomPath = workingDir + "/" + relativePathToEffectivePom;
        else
            pomPath = workingDir + "/pom.xml";

        listener.getLogger().println("::::::::::::::::::::::::::::::");
        listener.getLogger().println(pomPath);
        listener.getLogger().println("::::::::::::::::::::::::::::::");

        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        //slInfo.setEnabled(enable);
        slInfo.setEnabled(true);
        slInfo.setBuildName(String.valueOf(build.getNumber()));
        slInfo.setCustomerId(getDescriptor().getCustomerId());
        slInfo.setServerUrl(getDescriptor().getUrl());
        slInfo.setProxy(getDescriptor().getProxy());

        if (workspacepath != null && !"".equals(workspacepath))
            slInfo.setWorkspacepath(workspacepath);
        else
            slInfo.setWorkspacepath(workingDir);


        slInfo.setAppName(appName);
        slInfo.setModuleName(moduleName);
        slInfo.setBranchName(branch);
        slInfo.setFilesIncluded(filesincluded);
        slInfo.setFilesExcluded(filesexcluded);
        slInfo.setRecursive(recursive);
        slInfo.setPackagesIncluded(packagesincluded);
        //slInfo.setPackagesExcluded(packagesexcluded);

        slInfo.setListenerJar(testListenerJar);
        //slInfo.setListenerConfigFile(testListenerConfigFile);
        slInfo.setScannerJar(buildScannerJar);
        slInfo.setApiJar(apiJar);
        slInfo.setInheritedBuild(inheritedBuild);

        slInfo.setLogEnabled(logEnabled);
        slInfo.setLogLevel(logLevel);
        slInfo.setLogToFile(logToFile);
        slInfo.setLogFolder(logFolder);


        MavenIntegrationInfo info = new MavenIntegrationInfo();
        //info.setTestingFramework(testingFramework);
        info.setTestingFramework("testng");
        info.setSeaLightsPluginInfo(slInfo);
        info.setSourcePomFile(pomPath);

        MavenIntegration mavenIntegration = new MavenIntegration(listener.getLogger(), info);
        mavenIntegration.integrate();

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

//    public boolean isEnable() {
//        return enable;
//    }

    public String getAppName() {
        return appName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getBranch() {
        return branch;
    }

//    public String getTestingFramework()
//    {
//        return testingFramework;
//    }
//
//    public String getProjectType() {
//        return projectType;
//    }

    public String getPomPath() {
        return pomPath;
    }

    public String getPackagesincluded() {
        return packagesincluded;
    }

//    public String getPackagesexcluded() {
//        return packagesexcluded;
//    }

    public String getFilesincluded() {
        return filesincluded;
    }

    public String getFilesexcluded() {
        return filesexcluded;
    }

    public String getRelativePathToEffectivePom() {
        return relativePathToEffectivePom;
    }

    public String getWorkspacepath() {
        return workspacepath;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public String getBuildScannerJar() {
        return buildScannerJar;
    }

    public String getTestListenerJar() {
        return testListenerJar;
    }

//    public String getTestListenerConfigFile() {
//        return testListenerConfigFile;
//    }

    public boolean isInheritedBuild() {
        return inheritedBuild;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public boolean isLogToFile() {
        return logToFile;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public String getApiJar() {
        return apiJar;
    }

    private boolean isNullOrEmpty(String str)
    {
        return  (str == null || str.equals(""));
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        private String customerId;
        private String url;
        private String proxy;

        public DescriptorImpl() {
            super(SeaLightsJenkinsBuildWrapper.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Enable SeaLights integration";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            customerId = json.getString("customerId");
            url = json.getString("url");
            proxy = json.getString("proxy");
            save();
            return super.configure(req, json);
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

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }

        public ListBoxModel doFillProjectTypesItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Maven","");
            return items;
        }

//        public ListBoxModel doFillTestingFrameworkItems() {
//            ListBoxModel items = new ListBoxModel();
//            items.add("testng");
//            items.add("junit");
//            return items;
//        }


    }
}