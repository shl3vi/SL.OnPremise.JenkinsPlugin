package io.sealigths.plugins.sealightsjenkins.integration;

import hudson.FilePath;
import hudson.model.Computer;
import hudson.remoting.VirtualChannel;
import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import io.sealigths.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealigths.plugins.sealightsjenkins.utils.Logger;
import io.sealigths.plugins.sealightsjenkins.utils.StringUtils;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Created by Nadav on 4/19/2016.
 */
public class MavenIntegration {
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
                log.error("MavenIntegration.integrate - Unable to integrate sealights to pom : " + sourceFilename + ". Error:", e);
            }
        }

    }

    private void integrateToPomFile(FileBackupInfo fileBackupInfo, String sourceFilename, boolean shouldBackup) throws IOException, InterruptedException {
        PomFile pomFile = createPomFile(sourceFilename);

        if (pomFile.isPluginExistInEntirePom(SEALIGHTS_ARTIFACT_ID)) {
            log.info("MavenIntegration.integrate - Skipping the integration since SeaLights plugin is already defined in the the POM file.");
            return;
        }

        if (!pomFile.isValidPom()) {
            log.info("MavenIntegration.integrateToPomFile - Skipping SeaLights integration due to invalid pom. Pom: " + fileBackupInfo.getSourceFile());
            return;
        }

        if (shouldBackup) {
            backupPom(sourceFilename);
        }

        log.info("MavenIntegration.integrateToPomFile - About to modify pom: " + fileBackupInfo.getSourceFile());
        integrateToAllProfiles(fileBackupInfo, pomFile);
    }

    private PomFile createPomFile(String sourceFilename) {
        if (isJenkinsEnvironment)
            return new JenkinsPomFile(sourceFilename, log);
        return new PomFile(sourceFilename, log);
    }

    protected void backupPom(String sourceFileName) throws IOException, InterruptedException {
        String backupFile = sourceFileName + ".slbak";
        log.info("MavenIntegration.integrate - creating a back up file: " + backupFile);

        VirtualChannel channel = Computer.currentComputer().getChannel();
        FilePath sourceFile = new FilePath(channel, sourceFileName);
        FilePath targeFile = new FilePath(channel, backupFile);

        sourceFile.copyTo(targeFile);
    }

    private String getEventListenerPackage(TestingFramework testingFramework) {
        if ("testng".equalsIgnoreCase(testingFramework.name())) {
            return "io.sealights.onpremise.agents.java.agent.integrations.testng.TestListener";
        } else if ("junit_4".equalsIgnoreCase(testingFramework.name())) {
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
        if (!testingFramework.equals(TestingFramework.JUNIT_3)) {
            //# JUnit 3 doesn't need to add a listener to the pom (currently unsupported by Surefire).
//            pomFile.updateSurefirePlugin(testingFrameworkListeners, apiAgentPath);
        }
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