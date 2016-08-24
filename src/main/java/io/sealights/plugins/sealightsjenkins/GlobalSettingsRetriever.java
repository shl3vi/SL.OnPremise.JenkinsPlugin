package io.sealights.plugins.sealightsjenkins;

import hudson.FilePath;
import hudson.model.Computer;
import hudson.remoting.VirtualChannel;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.PathUtils;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nadav on 8/24/2016.
 * Allows Sealights to retrieve the Global Settings from the Hudson Maven Plugin
 */
public class GlobalSettingsRetriever {
    private final String HUDSON_MAVEN_FOLDER = "hudson.tasks.Maven_MavenInstallation";
    private final String SEALIGHTS_MAVEN_FOLDER = "io.sealights.plugins.sealightsjenkins.MavenSealightsBuildStep_MavenInstallation";
    private Logger logger;
    private String toolsPath;
    private String storageFolder;

    public GlobalSettingsRetriever(Logger logger, String toolsPath, String storageFolder)
    {
        this.logger = logger;
        this.toolsPath = toolsPath;
        this.storageFolder = storageFolder;
    }

    /*
    * Gets the global settings from a Maven Installatin of the Hudson plugin with the same name as the Sealighs one.<br/>
    * It starts by searching for an installation of a Master Jenkins. If found, uses it, else it search for a local one. <br/>
    *
    * @returns 'Settings.xml' path on local machine if one found, 'null' otherwise.
    * */
    public String retrieveSettingsFromHudsonMaven(MavenSealightsBuildStep.MavenInstallation mi, MavenBuildStepHelper mavenBuildStepHelper) throws IOException, InterruptedException {
        try{
            if (mi == null || mi.getHome() == null || Computer.currentComputer() == null)
                return null;

            String settingsPath = getSettingsFromMasterJenkins(mi, mavenBuildStepHelper);
            if (settingsPath != null)
                return settingsPath;

            //2. We couldn't find the Settings.xml on the master. If the current computer is master, no need to search again.
            if (Computer.currentComputer() instanceof Jenkins.MasterComputer)
                return null;

            //3. If we reached here, that's a slave. Use the one from the original Maven installation on the local machine.
            logger.info("Couldn't copy the 'settings.xml' file from master. Trying to use one from the original Maven installation on localhost.");
            settingsPath = getSettingsFromLocalMachine(mi);
            return settingsPath;
        }
        catch (Exception e)
        {
            logger.error("Failed to use the 'settings.xml' from the Hudson Maven plugin. Error:", e);
        }
        return null;
    }

    private String getSettingsFromLocalMachine(MavenSealightsBuildStep.MavenInstallation sealightsMavenInstallation) throws IOException, InterruptedException {
        String hudsonMavenHome = sealightsMavenInstallation.getHome().replace(SEALIGHTS_MAVEN_FOLDER, HUDSON_MAVEN_FOLDER);
        String pathToHudsonSettings = PathUtils.join(hudsonMavenHome, "conf", "settings.xml");
        VirtualChannel channel = Computer.currentComputer().getChannel();
        FilePath settingsFileUnderHudson = new FilePath(channel, pathToHudsonSettings);

        if (settingsFileUnderHudson.exists()){
            logger.info("Using 'settings.xml' Hudson's plugin. Settings path: '" + pathToHudsonSettings+ "'");
            return pathToHudsonSettings;
        }
        else{
            logger.info("Can't find settings.xml file from Hudson. Settings path:'" + pathToHudsonSettings+ "'");
        }
        return null;
    }


    private String getSettingsFromMasterJenkins(MavenSealightsBuildStep.MavenInstallation mi, MavenBuildStepHelper mavenBuildStepHelper) throws IOException, InterruptedException {
        if (mi == null || mavenBuildStepHelper == null)
            return null;

        String mavenName = mi.getName();
        String pathToSettingsXmlOnMaster = PathUtils.join(this.toolsPath, HUDSON_MAVEN_FOLDER, mavenName, "conf", "settings.xml");

        //1. Search for the Settings.xml file on the master. If found, copy it if needed and return the path.
        logger.info("Trying to copy 'settings.xml' from Hudson's Maven installation on the master Jenkins. Assumed path on master: '" + pathToSettingsXmlOnMaster + "'");
        FilePath settingsOnMaster = new FilePath(new File(pathToSettingsXmlOnMaster));
        if (settingsOnMaster.exists()){
            return mavenBuildStepHelper.copySettingsFileToSlave(pathToSettingsXmlOnMaster, this.storageFolder, logger);
        }
        return null;
    }
}
