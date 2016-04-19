package io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration;

/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
    public void execute()
    {
        PomFile pomFile= new PomFile("");
        if (pomFile.isPluginExist("sealights", "maven-plugin-name"))
        {
            //Sealights plugin is already defined. No need to redefine.
            return;
        }

        if (!pomFile.isPluginExist("2342", "surefire"))
        {
            //Surefire plugin isn't defined.
            throw new RuntimeException("SeaLights plugin requires Maven Surefire Plugin");
        }

        String version = pomFile.getPluginVersion("2342", "surefire");
        String[] tokens = version.split("\\.");
        int majorVersion = Integer.parseInt(tokens[0]);
        int minorVersion = Integer.parseInt(tokens[1]);
        if ((majorVersion < 2) || (majorVersion == 2 && minorVersion < 9))
        {
            throw new RuntimeException("Unsupported Maven Surefire plugin. SeaLights requires a version 2.9 and above.");
        }
    }
}
