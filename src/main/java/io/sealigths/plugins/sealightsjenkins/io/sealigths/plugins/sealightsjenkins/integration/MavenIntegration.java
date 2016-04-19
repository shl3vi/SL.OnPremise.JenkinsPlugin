package io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration;

import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
    private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";
    private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private PomFile pomFile;
    private  MavenIntegrationInfo info;

    public MavenIntegration(MavenIntegrationInfo info)
    {
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
        if (profileId == null || profileId.equals(""))
        {
            integrateToAllProfiles();
        }
        else
        {
            integrateToProfile(profileId);
        }
    }

    private void integrateToProfile(String profileId) {
        List<String> profiles = pomFile.getProfileIds();
        if (profileId.length() == 0)
        {
            throw new RuntimeException("The specified POM file does not contain any profiles.");
        }

        if (!profileId.contains(profileId))
        {
            throw new RuntimeException("The specified POM file does not contain a profile with id of '" + profileId + "'.");
        }


    }

    private void integrateToAllProfiles() {
        SeaLightsPluginInfo seaLightsPluginInfo = this.info.getSeaLightsPluginInfo();
        String xml = ("<artifactId>sl-plugin</artifactId>\n" +
                "<configuration><appName>#APP_NAME#</appName>\n<branchName>#BRANCH_NAME#</branchName></configuration>\n").replace("#APP_NAME#", seaLightsPluginInfo.getAppName())
                            .replace("#BRANCH_NAME#", seaLightsPluginInfo.getBranchName());
        pomFile.addPlugin(xml);
        try {
            pomFile.save("C:\\Work\\Projects\\SL.OnPremise.JenkinsPlugin\\src\\test\\cases\\MavenIntegration\\pom_new.xml");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
//        PomFile pomFile= new PomFile("C:\\Work\\Projects\\SL.OnPremise.JenkinsPlugin\\src\\test\\cases\\MavenIntegration\\pom.xml");
//        boolean pluginExist = pomFile.isPluginExist(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
//        String surefireVersion = pomFile.getPluginVersion(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
//        System.out.println("pluginExist:" + pluginExist + ", surefireVersion: " + surefireVersion);

        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setAppName("App Name");
        slInfo.setBranchName("Branch Name");
        MavenIntegrationInfo info = new MavenIntegrationInfo();
        info.setSeaLightsPluginInfo(slInfo);
        info.setPomFilePath("C:\\Work\\Projects\\SL.OnPremise.JenkinsPlugin\\src\\test\\cases\\MavenIntegration\\pom.xml");
        MavenIntegration mavenIntegration = new MavenIntegration(info);
        mavenIntegration.integrate();
    }
}
