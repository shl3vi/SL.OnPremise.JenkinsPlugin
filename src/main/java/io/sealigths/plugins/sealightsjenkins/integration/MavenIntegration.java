package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import io.sealigths.plugins.sealightsjenkins.utils.FileAndFolderUtils;
import io.sealigths.plugins.sealightsjenkins.utils.IncludeExcludeFilter;
import io.sealigths.plugins.sealightsjenkins.utils.StringUtils;

import javax.xml.transform.TransformerException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
    //TODO: Check the SureFire version on the resolved (effective) *.pom file.
    //private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";
    //private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private final static String SEALIGHTS_GROUP_ID = "io.sealights.on-premise.agents.plugin";
    private final static String SEALIGHTS_ARTIFACT_ID = "sealights-maven-plugin";


    private List<PomFile> poms;
    private MavenIntegrationInfo info;
    private PrintStream log;

    public MavenIntegration(PrintStream log, MavenIntegrationInfo info) {
        this.log = log;
        this.info = info;
    }

    private List<PomFile> getPoms(){
        SeaLightsPluginInfo slInfo = info.getSeaLightsPluginInfo();
        List<PomFile> pomFiles = new ArrayList<>();

        List<String> folders = Arrays.asList(slInfo.getBuildFilesFolders().split("\\s*,\\s*"));
        IncludeExcludeFilter filter  = new IncludeExcludeFilter(slInfo.getBuildFilesPatterns(), null);

        for (String folder: folders){
            List<String> matchingPoms = FileAndFolderUtils.findAllFilesWithFilter(folder, true, filter);
            for (String matchingPom : matchingPoms){
                pomFiles.add(new PomFile(matchingPom));
            }
        }

        return pomFiles;

    }

    public void integrate() {

        this.log.println("MavenIntegration.integrate - starting");
        poms = getPoms();

        for (PomFile pomFile : poms) {
            if (pomFile.isPluginExistInEntriePom(SEALIGHTS_GROUP_ID, SEALIGHTS_ARTIFACT_ID)) {
                this.log.println("MavenIntegration.integrate - Skipping the integration since SeaLights plugin is already defined in the the POM file.");
                return;
            }

            //TODO: Check the SureFire version on the resolved (effective) *.pom file.
//        if (!pomFile.isPluginExistInEntriePom(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID))
//        {
//            //Surefire plugin isn't defined.
//            throw new RuntimeException("SeaLights plugin requires Maven Surefire Plugin");
//        }
//
//        String version = pomFile.getPluginVersion(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
//        String[] tokens = version.split("\\.");
//        int majorVersion = Integer.parseInt(tokens[0]);
//        int minorVersion = Integer.parseInt(tokens[1]);
//        if ((majorVersion < 2) || (majorVersion == 2 && minorVersion < 9))
//        {
//            throw new RuntimeException("Unsupported Maven Surefire plugin. SeaLights requires a version 2.9 or higher.");
//        }

            String backupFile = this.info.getSourcePomFile() + ".slbak";
            this.savePom(backupFile);
            integrateToPomFile(pomFile);
        }
    }

    private void integrateToPomFile(PomFile pomFile) {
        String profileId = info.getProfileId();

        integrateToAllProfiles();
        //TODO: Enable the profile integration once done + tested.
//        if (profileId == null || profileId.equals("")) {
//            integrateToAllProfiles();
//        } else {
//            integrateToProfile(profileId);
//        }
    }

    private String getEventListenerPackage(TestingFramework testingFramework) {
        if ("testng".equalsIgnoreCase(testingFramework.name())) {
            return "io.sealights.onpremise.agents.java.agent.integrations.testng.TestListener";
        } else if ("junit".equalsIgnoreCase(testingFramework.name())) {
            return "io.sealights.onpremise.agents.java.agent.integrations.junit.SlRunListener";
        }
        return "";
    }

    private void integrateToProfile(String profileId, PomFile pomFile) {
        List<String> profileIdfiles = pomFile.getProfileIds();
        if (profileId.length() == 0)
        {
            throw new RuntimeException("The specified POM file does not contain any profiles.");
        }

        if (!profiles.contains(profileId))
        {
            throw new RuntimeException("The specified POM file does not contain a profile with id of '" + profileId + "'.");
        }

        SeaLightsPluginInfo seaLightsPluginInfo = this.info.getSeaLightsPluginInfo();
        String xml = seaLightsPluginInfo.toPluginText();
        pomFile.addPlugin(xml);

        String apiAgentPath = info.getSeaLightsPluginInfo().getApiJar();
        String testingFrameworkListeners = getEventListenerPackage(info.getTestingFramework());
        pomFile.updateSurefirePlugin(testingFrameworkListeners, apiAgentPath);

        savePom();

    }

    private void integrateToAllProfiles(PomFile pomFile) {
        SeaLightsPluginInfo seaLightsPluginInfo = this.info.getSeaLightsPluginInfo();
        String xml = seaLightsPluginInfo.toPluginText();
        pomFile.addPlugin(xml);

        String testingFrameworkListeners = getEventListenerPackage(info.getTestingFramework());
        String apiAgentPath = info.getSeaLightsPluginInfo().getApiJar();

        pomFile.updateSurefirePlugin(testingFrameworkListeners, apiAgentPath);

        savePom();
    }

    private void savePom() {
        String target = info.getTargetPomFile();
        if (target == null || target.equals(""))
        {
            info.setTargetPomFile(info.getSourcePomFile());
        }
        savePom(info.getTargetPomFile());
    }

    private void savePom(String filename) {
        try {
            pomFile.save(filename);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}