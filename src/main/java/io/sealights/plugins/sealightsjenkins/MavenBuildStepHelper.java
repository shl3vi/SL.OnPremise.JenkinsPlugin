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
import jenkins.mvn.GlobalSettingsProvider;
import jenkins.mvn.SettingsProvider;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;


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

    public void beginAnalysisBuildStep(AbstractBuild<?, ?> build, BuildListener listener, Logger logger, String pom) throws IOException, InterruptedException {
        if (!isSealightsEnabled)
            return;

        EnvVars envVars = build.getEnvironment(listener);
        beginAnalysis.perform(build, cleanupManager, logger, pom, envVars);
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

    public String copySettingsFileToSlave(String settingsPath, String filesStorage, Logger logger) throws IOException, InterruptedException {
        if (!this.isSealightsEnabled)
            return settingsPath;

        if (Computer.currentComputer() instanceof SlaveComputer) {
            String originalSettings = settingsPath;
            settingsPath = toTempSettingsFile(settingsPath, filesStorage);
            CustomFile customFile = new CustomFile(logger, cleanupManager, originalSettings);
            customFile.copyToSlave(settingsPath);
        }
        return settingsPath;
    }


    public void tryRestore(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        //Only the maven step has to restore as a single transaction.
        if (!this.currentMode.equals(BuildStepModes.InvokeMavenCommandWithSealights))
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
        settingsPath = UUID.randomUUID().toString() + "-" + Paths.get(settingsPath).getFileName().toString();
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
