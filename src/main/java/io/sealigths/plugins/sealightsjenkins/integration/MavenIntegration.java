package io.sealigths.plugins.sealightsjenkins.integration;

import javax.xml.transform.TransformerException;
import java.io.PrintStream;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
    private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";
    private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private PomFile pomFile;
    private MavenIntegrationInfo info;
    private PrintStream log;

    public MavenIntegration(PrintStream log, MavenIntegrationInfo info)
    {
        this.log = log;
        this.info = info;
        this.pomFile = new PomFile(info.getPomFilePath());
    }

    public void integrate()
    {

        if (pomFile.isPluginExist("sealights", "maven-plugin-name"))
        {
            //Sealights plugin is already defined. No need to redefine.
            return;
        }

        if (!pomFile.isPluginExist(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID))
        {
            //Surefire plugin isn't defined.
            throw new RuntimeException("SeaLights plugin requires Maven Surefire Plugin");
        }

        String version = pomFile.getPluginVersion(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
        String[] tokens = version.split("\\.");
        int majorVersion = Integer.parseInt(tokens[0]);
        int minorVersion = Integer.parseInt(tokens[1]);
        if ((majorVersion < 2) || (majorVersion == 2 && minorVersion < 9))
        {
            throw new RuntimeException("Unsupported Maven Surefire plugin. SeaLights requires a version 2.9 or higher.");
        }

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
        log.println("*************************************");
        log.println("*************************************");
        log.println(pomFile.getPomAsString());
        log.println("*************************************");
        log.println("*************************************");
        String xml = seaLightsPluginInfo.toPluginText();
        log.println(xml);
        pomFile.addPlugin(xml);

        String eventListenerNode = addListenerToSurefire(testingFrameWorkListeners);
        pomFile.addEventListener(eventListenerNode);

        try {
            pomFile.save(info.getPomFilePath());
            PomFile pomFile1 = new PomFile(info.getPomFilePath());
            log.println("*************************************");
            log.println("*************************************");
            log.println(pomFile1.getPomAsString());
            log.println("*************************************");
            log.println("*************************************");
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

    public static void main(String[] args)
    {
//        PomFile pomFile= new PomFile("C:\\Work\\Projects\\SL.OnPremise.JenkinsPlugin\\src\\test\\cases\\MavenIntegration\\pom.xml");
//        boolean pluginExist = pomFile.isPluginExist(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
//        String surefireVersion = pomFile.getPluginVersion(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
//        System.out.println("pluginExist:" + pluginExist + ", surefireVersion: " + surefireVersion);

//        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
//        slInfo.setAppName("App Name");
//        slInfo.setBranchName("Branch Name");
//        MavenIntegrationInfo info = new MavenIntegrationInfo();
//        info.setSeaLightsPluginInfo(slInfo);
//        info.setPomFilePath("C:\\Work\\Projects\\SL.OnPremise.JenkinsPlugin\\src\\test\\cases\\MavenIntegration\\pom.xml");
//        MavenIntegration mavenIntegration = new MavenIntegration(info);
//        mavenIntegration.integrate();
    }
}
