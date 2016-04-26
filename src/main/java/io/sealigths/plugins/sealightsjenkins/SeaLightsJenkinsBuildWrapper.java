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
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;
import io.sealigths.plugins.sealightsjenkins.integration.JarsHelper;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegration;
import io.sealigths.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealigths.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealigths.plugins.sealightsjenkins.language.Language;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.util.Map;

public class SeaLightsJenkinsBuildWrapper extends BuildWrapper {

//    private final TechIntegration integrations;

    private final String appName;
    private final String moduleName;
    private final String branch;

    private final String pomPath;
    private final String environment;
    private final String packagesincluded;
    private final String packagesexcluded;
    private final String filesincluded;
    private final String filesexcluded;
    private final String relativePathToEffectivePom;
    private final boolean recursive;
    private final String workspacepath;
    private final String buildScannerJar;
    private final String testListenerJar;
    private final String apiJar;
    private final String testListenerConfigFile;
    private final boolean inheritedBuild;

    private boolean logEnabled;
    private LogDestination logDestination = LogDestination.CONSOLE;
    private final String logFolder;

    private TestingFramework testingFramework = TestingFramework.TESTNG;
    private LogLevel logLevel = LogLevel.OFF;
//    private Language language = Language.JAVA;
    private ProjectType projectType = ProjectType.MAVEN;

    @DataBoundConstructor
    public SeaLightsJenkinsBuildWrapper(String appName, String moduleName, String branch, String pomPath,
                                        @NonNull TestingFramework testingFramework,
                                        String packagesincluded, String packagesexcluded,
                                        String filesincluded, String filesexcluded,
                                        String relativePathToEffectivePom, boolean recursive,
                                        String workspacepath, String testListenerConfigFile,
                                        String buildScannerJar, String testListenerJar, String apiJar,
                                        boolean inheritedBuild, String environment,@NonNull ProjectType projectType,
                                        boolean logEnabled, @NonNull LogLevel logLevel, @NonNull LogDestination logDestination, String logFolder
                                        ,TechIntegration integrations,@NonNull Language language) throws IOException {

//        this.integrations = integrations;
        this.appName = appName;
        this.moduleName = moduleName;
        this.branch = branch;
        this.pomPath = pomPath;
        this.packagesincluded = packagesincluded;
        this.packagesexcluded = packagesexcluded;
        this.filesincluded = filesincluded;
        this.filesexcluded = filesexcluded;
        this.relativePathToEffectivePom = relativePathToEffectivePom;
        this.recursive = recursive;
        this.workspacepath = workspacepath;
        this.testListenerConfigFile = testListenerConfigFile;
        this.inheritedBuild = inheritedBuild;

        this.environment = environment;

        this.testingFramework = testingFramework;

        this.projectType = projectType;

//        this.language = language;

        this.logEnabled = logEnabled;
        this.logLevel = logLevel;
        this.logDestination = logDestination;
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

        listener.getLogger().println("-----------Sealights Jenkins Plugin Configuration--------------");
        listener.getLogger().println("testing framework: " + testingFramework);
        listener.getLogger().println("branch: " + branch);
        listener.getLogger().println("appName:" +appName);
        listener.getLogger().println("moduleName:" +moduleName);
        listener.getLogger().println("recursive: " + recursive);
        listener.getLogger().println("workspacepath: " + workspacepath);
        listener.getLogger().println("environment: " + environment);
        //listener.getLogger().println("projectType:" +projectType);
        listener.getLogger().println("pomPath:" +pomPath);
        listener.getLogger().println("packagesincluded:" +packagesincluded);
        listener.getLogger().println("packagesexcluded:" +packagesexcluded);
        listener.getLogger().println("filesincluded:" +filesincluded);
        listener.getLogger().println("filesexcluded:" +filesexcluded);
        listener.getLogger().println("buildScannerJar:" +buildScannerJar);
        listener.getLogger().println("testListenerJar:" +testListenerJar);
        listener.getLogger().println("testListenerConfigFile :" +testListenerConfigFile);
        listener.getLogger().println("inheried: " + inheritedBuild);
        listener.getLogger().println("apiJar:" +apiJar);
        listener.getLogger().println("project Type : " + projectType);
        listener.getLogger().println("LogEnabled:" + logEnabled);
        listener.getLogger().println("logDestination:" + logDestination);
        listener.getLogger().println("logLevel:" +logLevel);
        listener.getLogger().println("logFolder:" +logFolder);
        listener.getLogger().println("-----------Sealights Jenkins Plugin Configuration--------------");

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
        slInfo.setPackagesExcluded(packagesexcluded);

        slInfo.setListenerJar(testListenerJar);
        slInfo.setListenerConfigFile(testListenerConfigFile);
        slInfo.setScannerJar(buildScannerJar);
        slInfo.setApiJar(apiJar);
        slInfo.setInheritedBuild(inheritedBuild);

        slInfo.setEnvironment(environment);

        slInfo.setLogEnabled(!("Off".equalsIgnoreCase(logLevel.getDisplayName())));
        slInfo.setLogLevel(logLevel);
        slInfo.setLogDestination(logDestination);
        slInfo.setLogFolder(logFolder);


        MavenIntegrationInfo info = new MavenIntegrationInfo();
        info.setTestingFramework(testingFramework);
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

//    public TechIntegration getIntegrations() {
//        return integrations;
//    }

    public String getAppName() {
        return appName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getBranch() {
        return branch;
    }

//
//    public String getProjectType() {
//        return projectType;
//    }


//    public Language getLanguage() {
//        return language;
//    }
//
//    public void setLanguage(Language language) {
//        this.language = language;
//    }

    public String getPomPath() {
        return pomPath;
    }

    public String getPackagesincluded() {
        return packagesincluded;
    }

    public String getPackagesexcluded() {
        return packagesexcluded;
    }

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

    public String getTestListenerConfigFile() {
        return testListenerConfigFile;
    }

    public boolean isInheritedBuild() {
        return inheritedBuild;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LogDestination getLogDestination() {
        return logDestination;
    }

    public void setLogDestination(LogDestination logDestination) {
        this.logDestination = logDestination;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public String getApiJar() {
        return apiJar;
    }

    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    public void setTestingFramework(TestingFramework testingFramework) {
        this.testingFramework = testingFramework;
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
            items.add("MAVEN","");
            return items;
        }

        public ComboBoxModel doFillTheTypeItems(@QueryParameter ProjectType projectType) {
            switch (projectType.getDisplayName()) {
                case "maven":
                    return new ComboBoxModel("Come Together","Something","I Want You");
                default:
                    return new ComboBoxModel("The One After 909","Rocker","Get Back");
            }
        }

//        public ListBoxModel doFillTestingFrameworkItems() {
//            ListBoxModel items = new ListBoxModel();
//            items.add("testng");
//            items.add("junit");
//            return items;
//        }


    }
}