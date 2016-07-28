package io.sealights.plugins.sealightsjenkins;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.slaves.SlaveComputer;
import hudson.tasks._maven.MavenConsoleAnnotator;
import hudson.tools.ToolLocationNodeProperty;
import hudson.util.ArgumentListBuilder;
import hudson.util.DescribableList;
import hudson.util.VariableResolver;
import io.sealights.plugins.sealightsjenkins.enums.BuildStepModes;
import io.sealights.plugins.sealightsjenkins.integration.JarsHelper;
import io.sealights.plugins.sealightsjenkins.utils.CustomFile;
import io.sealights.plugins.sealightsjenkins.integration.SealightsMavenPluginHelper;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Created by Nadav on 6/5/2016.
 */
public class MavenBuildStepHelper {
    private boolean isSealightsEnabled;
    private BuildStepModes currentMode;
    private CleanupManager cleanupManager;
    private BeginAnalysis beginAnalysis;


    public MavenBuildStepHelper(BuildStepModes currentMode, CleanupManager cleanupManager, BeginAnalysis beginAnalysis) {
        this.isSealightsEnabled = (currentMode.equals(BuildStepModes.PrepareSealights) || currentMode.equals(BuildStepModes.InvokeMavenCommandWithSealights));
        this.cleanupManager = cleanupManager;
        this.beginAnalysis = beginAnalysis;
        this.currentMode = currentMode;
    }

    public void installSealightsMavenPlugin(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, String pom, String properties, MavenSealightsBuildStep mavenBuildStep, String filesStorage) throws IOException, InterruptedException {
        if (!isSealightsEnabled)
            return;

        Logger logger = new Logger(listener.getLogger());

        String slMavenPluginJar = JarsHelper.loadJarAndSaveAsTempFile(SealightsMavenPluginHelper.SL_MVN_JAR_NAME, filesStorage);
        CustomFile customFile = new CustomFile(logger, cleanupManager, slMavenPluginJar);
        customFile.copyToSlave();

        SealightsMavenPluginHelper pluginHelper = new SealightsMavenPluginHelper(logger);
        String normalizedTarget = pluginHelper.getPluginInstallationCommand(slMavenPluginJar);
        logger.info("Installing sealights-maven plugin");
        logger.info("Command: " + normalizedTarget);

        invokeMavenCommand(build, launcher, listener, normalizedTarget, logger, pom, properties, mavenBuildStep);
    }

    public boolean beginAnalysisBuildStep(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, Logger logger, String pom, String additionalMavenArguments, String properties, MavenSealightsBuildStep mavenBuildStep) throws IOException, InterruptedException {
        if (!isSealightsEnabled)
            return true;

        EnvVars envVars = build.getEnvironment(listener);
        Map<String, String> metadata = JenkinsUtils.createMetadataFromEnvVars(envVars);
        beginAnalysis.perform(build, cleanupManager, logger, pom, metadata);
        return true;
    }

    public MavenSealightsBuildStep.MavenInstallation overrideMavenHomeIfNeed(MavenSealightsBuildStep.MavenInstallation mavenInstallation, Logger logger) {
        if (!isSealightsEnabled)
            return mavenInstallation;

        ToolLocationNodeProperty.ToolLocation nodeTools = getMavenNodeToolsForCurrentComputer(logger, mavenInstallation.getName());
        if (nodeTools != null) {
            //If the computer has node tools, update the maven installation to use the 'home' value from the tools.
            return new MavenSealightsBuildStep.MavenInstallation(mavenInstallation.getName(), nodeTools.getHome(), mavenInstallation.getProperties().toList());

        }
        return mavenInstallation;
    }

    private boolean invokeMavenCommand(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, String normalizedTarget, Logger logger, String projectPom, String properties, MavenSealightsBuildStep mavenBuildStep) throws IOException, InterruptedException {
        VariableResolver<String> vr = build.getBuildVariableResolver();
        EnvVars env = build.getEnvironment(listener);
        String pom = env.expand(projectPom);
        ArgumentListBuilder args = new ArgumentListBuilder();
        MavenSealightsBuildStep.MavenInstallation mi = mavenBuildStep.getMaven();

        if (mi == null) {
            String execName = build.getWorkspace().act(new DecideDefaultMavenCommand(normalizedTarget));
            args.add(execName);

        } else {
            mi = mi.forNode(Computer.currentComputer().getNode(), listener);
            mi = overrideMavenHomeIfNeed(mi, logger);
            mi = mi.forEnvironment(env);
            String exec = mi.getExecutable(launcher);
            if (exec == null) {
                listener.fatalError("Couldn't find any executable in " + mi.getHome()/*Messages.Maven_NoExecutable(mi.getHome())*/);
                return false;
            }
            args.add(exec);
        }
        if (pom != null)
            args.add("-f", pom);

//
//        if(!S_PATTERN.matcher(targets).find()){ // check the given target/goals do not contain settings parameter already
//            String settingsPath = SettingsProvider.getSettingsRemotePath(getSettings(), build, listener);
//            if(StringUtils.isNotBlank(settingsPath)){
//                args.add("-s", settingsPath);
//            }
//        }
//        if(!GS_PATTERN.matcher(targets).find()){
//            String settingsPath = GlobalSettingsProvider.getSettingsRemotePath(getGlobalSettings(), build, listener);
//            if(StringUtils.isNotBlank(settingsPath)){
//                args.add("-gs", settingsPath);
//            }
//        }

        Set<String> sensitiveVars = build.getSensitiveBuildVariables();

        args.addKeyValuePairs("-D", build.getBuildVariables(), sensitiveVars);
        final VariableResolver<String> resolver = new VariableResolver.Union<String>(new VariableResolver.ByMap<String>(env), vr);
        args.addKeyValuePairsFromPropertyString("-D", properties, resolver, sensitiveVars);
        if (mavenBuildStep.usesPrivateRepository()) {
            args.add("-Dmaven.repo.local=" + build.getWorkspace().child(".repository"));

        }
        args.addTokenized(normalizedTarget);

        mavenBuildStep.sealightsWrapUpArguments(args, normalizedTarget, build, launcher, listener);
        mavenBuildStep.sealightsBuildEnvVars(env, mi);

        if (!launcher.isUnix()) {
            args = args.toWindowsCommand();
        }
        try {
            MavenConsoleAnnotator mca = new MavenConsoleAnnotator(listener.getLogger(), build.getCharset());
            int r = launcher.launch().cmds(args).envs(env).stdout(mca).pwd(build.getModuleRoot()).join();
            if (0 != r) {
                return false;
            }
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            e.printStackTrace(listener.fatalError("command execution failed"/*Messages.Maven_ExecFailed()*/));
            return false;
        }
        return true;
    }

    public void copySettingsFileToSlave(String settingsPath, String filesStorage, Logger logger) throws IOException, InterruptedException {
        if (!this.isSealightsEnabled)
            return;

        if (Computer.currentComputer() instanceof SlaveComputer) {
            String originalSettings = settingsPath;
            settingsPath = toTempSettingsFile(settingsPath, filesStorage);
            CustomFile customFile = new CustomFile(logger, cleanupManager ,originalSettings);
            customFile.copyToSlave(settingsPath);
        }
    }


    public void tryRestore(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        //Only the maven step has to restore as a single transaction.
        if(!this.currentMode.equals(BuildStepModes.InvokeMavenCommandWithSealights))
            return;

        if (beginAnalysis.isAutoRestoreBuildFile()) {
            cleanupManager.clean();
            restoreBuildFile(build, launcher, listener);
        }
    }

    private void restoreBuildFile(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        RestoreBuildFile restoreBuildFile = new RestoreBuildFile(beginAnalysis.isAutoRestoreBuildFile(), beginAnalysis.getBuildFilesFolders(), beginAnalysis.getPomPath());
        restoreBuildFile.perform(build, launcher, listener);
    }

    private String toTempSettingsFile(String settingsPath, String filesStorage) {
        settingsPath =  UUID.randomUUID().toString() + "-" + Paths.get(settingsPath).getFileName().toString();
        String osTempFolder = System.getProperty("java.io.tmpdir");
        if (StringUtils.isNotEmpty(filesStorage))
            osTempFolder = filesStorage;
        String tempFile = Paths.get(osTempFolder, settingsPath).toAbsolutePath().toString();
        settingsPath = tempFile;
        return settingsPath;
    }


    private ToolLocationNodeProperty.ToolLocation getMavenNodeToolsForCurrentComputer(Logger logger, String mavenName) {
        List<Node> nodes = Jenkins.getInstance().getNodes();
        for (Node node : nodes) {
            String nodeName = node.getNodeName();
            String computerName = Computer.currentComputer().getName();
            logger.info("Current node:" + nodeName + ", Computer name:" + computerName);

            if (!nodeName.equalsIgnoreCase(computerName))
                continue;

            DescribableList<NodeProperty<?>, NodePropertyDescriptor> nodeProperties = node.getNodeProperties();
            for (NodeProperty prop : nodeProperties) {
                if (!(prop instanceof ToolLocationNodeProperty))
                    continue;

                ToolLocationNodeProperty toolsLocation = (ToolLocationNodeProperty) prop;
                for (ToolLocationNodeProperty.ToolLocation location : toolsLocation.getLocations()) {
                    if (location.getName().equalsIgnoreCase(mavenName) && location.getType() instanceof hudson.tasks.Maven.MavenInstallation.DescriptorImpl)
                        return location;
                }
            }
        }
        return null;
    }

}
