package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.ExecutionType;
import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import io.sealigths.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealigths.plugins.sealightsjenkins.utils.Logger;


/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
    //TODO: Check the SureFire version on the resolved (effective) *.pom file.
    //private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";
    //private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";
    private final static String SEALIGHTS_GROUP_ID = "io.sealights.on-premise.agents.plugin";
    private final static String SEALIGHTS_ARTIFACT_ID = "sealights-maven-plugin";


    private MavenIntegrationInfo mavenIntegrationInfo;
    private Logger log;
    private boolean isJenkinsEnvironment;

    public MavenIntegration(Logger log, MavenIntegrationInfo mavenIntegrationInfo) {
        this(log, mavenIntegrationInfo, true);
    }

    public MavenIntegration(Logger log, MavenIntegrationInfo mavenIntegrationInfo, boolean isJenkinsEnvironment) {
        this.log = log;
        this.mavenIntegrationInfo = mavenIntegrationInfo;
        this.isJenkinsEnvironment = isJenkinsEnvironment;
    }

    public void integrate() {
        this.integrate(true);
    }

    public void integrate(boolean shouldBackup) {

        log.info("MavenIntegration.integrate - starting");

        for (FileBackupInfo fileBackupInfo : mavenIntegrationInfo.getPomFiles()) {
            String sourceFilename = fileBackupInfo.getSourceFile();

            try {
                integrateToPomFile(fileBackupInfo, sourceFilename, shouldBackup);
            } catch (Exception e) {
                log.error("MavenIntegration.integrate - Unable to parse pom : " + sourceFilename + ". Error:", e);
            }
        }

    }

    private void integrateToPomFile(FileBackupInfo fileBackupInfo, String sourceFilename, boolean shouldBackup) {

        PomFile pomFile = createPomFile(sourceFilename);

        if (pomFile.isPluginExistInEntirePom(SEALIGHTS_ARTIFACT_ID)) {
            log.info("MavenIntegration.integrate - Skipping the integration since SeaLights plugin is already defined in the the POM file.");
            return;
        }
//                TODO: Check the SureFire version on the resolved (effective) *.pom file.
//            if (!pomFile.isPluginExistInEntirePom(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID))
//            {
//                //Surefire plugin isn't defined.
//                throw new RuntimeException("SeaLights plugin requires Maven Surefire Plugin");
//            }
//
//            String version = pomFile.getPluginVersion(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
//            String[] tokens = version.split("\\.");
//            int majorVersion = Integer.parseInt(tokens[0]);
//            int minorVersion = Integer.parseInt(tokens[1]);
//            if ((majorVersion < 2) || (majorVersion == 2 && minorVersion < 9))
//            {
//                throw new RuntimeException("Unsupported Maven Surefire plugin. SeaLights requires a version 2.9 or higher.");
//            }

        if (!pomFile.isValidPom()) {
            log.info("MavenIntegration.integrateToPomFile - Skipping SeaLights integration due to invalid pom. Pom: " + fileBackupInfo.getSourceFile());
            return;
        }

        if (shouldBackup) {
            backupPom(sourceFilename, pomFile);
        }

        log.info("MavenIntegration.integrateToPomFile - About to modify pom: " + fileBackupInfo.getSourceFile());
        integrateToAllProfiles(fileBackupInfo, pomFile);
    }

    private PomFile createPomFile(String sourceFilename) {
        if (isJenkinsEnvironment)
            return new JenkinsPomFile(sourceFilename, log);
        return new PomFile(sourceFilename, log);
    }

    private void backupPom(String sourceFile, PomFile pom) {
        String backupFile = sourceFile + ".slbak";
        log.info("MavenIntegration.integrate - creating a back up file: " + backupFile);
        this.savePom(backupFile, pom);
    }

    private String getEventListenerPackage(TestingFramework testingFramework) {
        if ("testng".equalsIgnoreCase(testingFramework.name())) {
            return "io.sealights.onpremise.agents.java.agent.integrations.testng.TestListener";
        } else if ("junit".equalsIgnoreCase(testingFramework.name())) {
            return "io.sealights.onpremise.agents.java.agent.integrations.junit.SlRunListener";
        }
        return "";
    }


    private void integrateToAllProfiles(FileBackupInfo fileBackupInfo, PomFile pomFile) {
        SeaLightsPluginInfo seaLightsPluginInfo = this.mavenIntegrationInfo.getSeaLightsPluginInfo();
        TestingFramework testingFramework = mavenIntegrationInfo.getTestingFramework();
        SealightsMavenPluginHelper slHelper = new SealightsMavenPluginHelper(log);
        String xml = slHelper.toPluginText(seaLightsPluginInfo, testingFramework);

        pomFile.addPlugin(xml);

        String testingFrameworkListeners = getEventListenerPackage(testingFramework);

        String apiAgentPath = mavenIntegrationInfo.getSeaLightsPluginInfo().getApiJar();

        if (testingFramework.equals(TestingFramework.AUTO_DETECT)) {
            testingFrameworkListeners = null; //Used to pass control to the maven plugin.
        }
        pomFile.updateSurefirePlugin(testingFrameworkListeners, apiAgentPath);

        savePom(fileBackupInfo, pomFile);
    }

    private void savePom(FileBackupInfo fileBackupInfo, PomFile pomFile) {
        String targetFile = fileBackupInfo.getTargetFile();
        if (targetFile == null || targetFile.equals("")) {
            targetFile = fileBackupInfo.getSourceFile();
        }
        savePom(targetFile, pomFile);
    }

    private void savePom(String filename, PomFile pomFile) {
        try {
            pomFile.save(filename);
        } catch (Exception e) {
            log.error("Failed saving POM file. Error:", e);
        }
    }






}