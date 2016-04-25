package io.sealigths.plugins.sealightsjenkins.integration;

import javax.xml.transform.TransformerException;
import java.io.PrintStream;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
    private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";
    private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private final static String SEALIGHTS_GROUP_ID = "io.sealights.on-premise.agents.plugin";
    private final static String SEALIGHTS_ARTIFACT_ID = "sealights-maven-plugin";



    private PomFile pomFile;
    private MavenIntegrationInfo info;
    private PrintStream log;

    public MavenIntegration(PrintStream log, MavenIntegrationInfo info)
    {
        this.log = log;
        this.info = info;
        this.pomFile = new PomFile(info.getSourcePomFile());
    }

    public void integrate()
    {

        this.log.println("MavenIntegration.integrate - starting");
        if (pomFile.isPluginExistInEntriePom(SEALIGHTS_GROUP_ID, SEALIGHTS_ARTIFACT_ID))
        {
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

        integrateToPomFile();
    }

    private void integrateToPomFile() {
        String profileId = info.getProfileId();
        String eventListenerPackage = getEventListenerPackage(info.getTestingFramework());
        if (profileId == null || profileId.equals(""))
        {
            integrateToAllProfiles(eventListenerPackage);
        }
        else
        {
            integrateToProfile(profileId);
        }
    }

    private String getEventListenerPackage(String testingFramework){
        if ("testng".equalsIgnoreCase(testingFramework)){
            return "io.sealights.onpremise.agents.java.agent.integrations.testng.TestListener";
        }else if ("junit".equalsIgnoreCase(testingFramework)){
            return "io.sealights.onpremise.agents.java.agent.integrations.junit.SlRunListener";
        }
        return "";
    }

    private void integrateToProfile(String profileId) {
//        List<String> profiles = pomFile.getProfileIds();
//        if (profileId.length() == 0)
//        {
//            throw new RuntimeException("The specified POM file does not contain any profiles.");
//        }
//
//        if (!profileId.contains(profileId))
//        {
//            throw new RuntimeException("The specified POM file does not contain a profile with id of '" + profileId + "'.");
//        }
//

    }

    private void integrateToAllProfiles(String testingFrameWorkListeners) {
        SeaLightsPluginInfo seaLightsPluginInfo = this.info.getSeaLightsPluginInfo();
        String xml = seaLightsPluginInfo.toPluginText();
        pomFile.addPlugin(xml);

        String eventListenerNode = addListenerToSurefire(testingFrameWorkListeners);
        pomFile.addTestingFrameworkListener(eventListenerNode);

        try {
            String target = info.getTargetPomFile();
            if (target == null || target.equals(""))
                throw new RuntimeException("Target file is null or empty.");

            pomFile.save(info.getTargetPomFile());
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public String addListenerToSurefire(String testingFrameWorkListeners) {
        StringBuilder sureFireProperty = new StringBuilder();
        sureFireProperty.append("<properties>");
        sureFireProperty.append("<property>");
        sureFireProperty.append("<name>listener</name>");
        sureFireProperty.append("<value>");
        sureFireProperty.append(testingFrameWorkListeners);
        sureFireProperty.append("</value>");
        sureFireProperty.append("</property>");
        sureFireProperty.append("</properties>");

        return sureFireProperty.toString();
    }


}
