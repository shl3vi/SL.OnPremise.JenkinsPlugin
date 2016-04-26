package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.LogLevel;
import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class MavenIntegrationTest {

    String PATH = System.getProperty("user.dir") + "/src/test/cases/MavenIntegration/";

    @Test
    public void InjectSeaLightsPluginWithTestngListenerToAPomWithoutThePluginButWithSurefire() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.TESTNG;
        String TEST_CASE = "1_Inject_SeaLights_plugin_with_testng_listener_to_a_pom_without_the_plugin_but_with_surefire";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }

    @Test
    public void InjectSeaLightsPluginWithJunitListenerToAPomWithoutThePluginButWithSurefire() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.JUNIT;
        String TEST_CASE = "2_Inject_SeaLights_plugin_with_junit_listener_to_a_pom_without_the_plugin_but_with_surefire";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }

    @Test
    public void InjectSeaLightsPluginToAPomWithSingleProfileWithSurefire() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.JUNIT;
        String TEST_CASE = "3_Inject_SeaLights_plugin_to_a_pom_with_a_single_profile_with_surefire";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }

    @Test
    public void InjectSeaLightsPluginToAPomWithTwoProfilesWithSurefire() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.JUNIT;
        String TEST_CASE = "4_Inject_SeaLights_plugin_to_a_pom_with_a_two_profiles_with_surefire";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }

    @Test
    public void InjectSeaLightsPluginToAPomWithTwoProfilesWithSurefireAndAnotherSurefireNotInProfile() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.JUNIT;
        String TEST_CASE = "5_Inject_SeaLights_plugin_to_a_pom_with_a_two_profiles_with_surefire_and_another_surefire_not_in_profile";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }

    @Test
    public void InjectSeaLightsPluginToAPomWithProfileWhichHasSurefireAndAnotherDoesnt() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.JUNIT;
        String TEST_CASE = "6_Inject_SeaLights_plugin_to_a_pom_with_a_profile_which_has_surefire_and_another_doesnt";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }

    @Test
    public void InjectSeaLightsPluginToAPomWithSurefireThatHasExistingConfigurationElement() throws Exception {

        //Arrange
        TestingFramework TESTING_FRAMEWORK=TestingFramework.JUNIT;
        String TEST_CASE = "7_Inject_SeaLights_plugin_to_a_pom_with_surefire_that_has_existing_configuration_element";
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new PrintStream(System.out),mavenIntegrationInfo);

        //Act
        mavenIntegration.integrate();

        //Assert
        String expected = readFileAndTrim(testFolder + "/expected.xml");
        String actual = readFileAndTrim(testFolder + "/actual.xml");
        Assert.assertEquals("Expected to have a different POM file.", expected, actual);
    }



    private String readFileAndTrim(String filepath) throws IOException {
        String s = FileUtils.readFileToString(new File(filepath));
        return trim(s);
    }

    public static String trim(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        StringBuffer result = new StringBuffer();
        try {
            String line;
            while ( (line = reader.readLine() ) != null) {
                String trim = line.trim();
                trim = trim.replaceAll(">\\s*<", "><").trim();
                result.append(trim);
            }
            return result.toString().trim().replaceAll(">\\s*<", "><");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MavenIntegrationInfo createDefaultMavenIntegrationInfo(String path){


        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setEnabled(true);
        slInfo.setBuildName("1");
        slInfo.setCustomerId("fake-customer-id-123");
        slInfo.setServerUrl("http://fake-server-url.com");

        slInfo.setWorkspacepath("c:\\fake-worakpsacepath");


        slInfo.setAppName("fake-app-name");
        slInfo.setModuleName("fake-module-name");
        slInfo.setBranchName("fake-branch");
        slInfo.setFilesIncluded("*.class");
        slInfo.setRecursive(true);
        slInfo.setPackagesIncluded("com.fake.*");
        slInfo.setPackagesExcluded("com.fake.excluded.*");

        slInfo.setListenerJar("c:\\fake-test-listener.jar");
        slInfo.setScannerJar("c:\\fake-build-scanner.jar");
        slInfo.setApiJar("c:\\fake-api.jar");
        slInfo.setInheritedBuild(true);

        slInfo.setLogEnabled(false);
        slInfo.setLogLevel(LogLevel.INFO);
        slInfo.setLogFolder("c:\\fake-log-folder");


        MavenIntegrationInfo info = new MavenIntegrationInfo();
        info.setTestingFramework(TestingFramework.TESTNG);
        info.setSeaLightsPluginInfo(slInfo);
        info.setSourcePomFile(path + "/pom.xml");
        info.setTargetPomFile(path + "/actual.xml");

        return info;
    }

    private String getTestFolder(String testCaseName)
    {
        return PATH + testCaseName;
    }
}