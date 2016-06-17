package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.BuildStrategy;
import io.sealigths.plugins.sealightsjenkins.ExecutionType;
import io.sealigths.plugins.sealightsjenkins.LogLevel;
import io.sealigths.plugins.sealightsjenkins.TestingFramework;
import io.sealigths.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealigths.plugins.sealightsjenkins.utils.Logger;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MavenIntegrationTest {

    String PATH = System.getProperty("user.dir") + "/src/test/cases/MavenIntegration/";
    boolean SAVE_POM_USING_JENKINS_API = false;

    @Test
    public void injectSeaLightsPluginWithTestngListenerToAPomWithoutThePluginButWithSurefire() throws Exception {
        performTest(
                "1_Inject_SeaLights_plugin_with_testng_listener_to_a_pom_without_the_plugin_but_with_surefire", TestingFramework.TESTNG);
    }

    @Test
    public void injectSeaLightsPluginWithJunitListenerToAPomWithoutThePluginButWithSurefire() throws Exception {
        performTest("2_Inject_SeaLights_plugin_with_junit_listener_to_a_pom_without_the_plugin_but_with_surefire");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithSingleProfileWithSurefire() throws Exception {
        performTest("3_Inject_SeaLights_plugin_to_a_pom_with_a_single_profile_with_surefire");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithTwoProfilesWithSurefire() throws Exception {
        performTest("4_Inject_SeaLights_plugin_to_a_pom_with_a_two_profiles_with_surefire");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithTwoProfilesWithSurefireAndAnotherSurefireNotInProfile() throws Exception {
        performTest("5_Inject_SeaLights_plugin_to_a_pom_with_a_two_profiles_with_surefire_and_another_surefire_not_in_profile");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithProfileWhichHasSurefireAndAnotherDoesnt() throws Exception {
        performTest("6_Inject_SeaLights_plugin_to_a_pom_with_a_profile_which_has_surefire_and_another_doesnt");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithSurefireThatHasExistingConfigurationElement() throws Exception {
        performTest("7_Inject_SeaLights_plugin_to_a_pom_with_surefire_that_has_existing_configuration_element");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithSurefireThatHasExistingConfigurationElementWithArgLineElementThatDoesntChainOldValues() throws Exception {
        performTest("8_Inject_SeaLights_plugin_to_a_pom_with_surefire_that_has_existing_configuration_element_with_argLine_element_that_doesnt_chain_old_values");
    }


    @Test
    public void injectSeaLightsPluginToAPomWithSurefireThatHasExistingConfigurationElementWithArgLineElementThatDoesChainOldValues() throws Exception {
        performTest("9_Inject_SeaLights_plugin_to_a_pom_with_surefire_that_has_existing_configuration_element_with_argLine_element_that_does_chain_old_values");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithSurefireInsidePluginManagementElement() throws Exception {
        performTest("10_Inject_SeaLights_plugin_to_a_pom_with_surefire_inside_pluginManagement_element");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithAdditionalClassPathElementInsideSurefire() throws Exception {
        performTest("11_Inject_SeaLights_plugin_to_a_pom_with_additionalClasspathElement_inside_surefire");
    }

    @Test
    public void insertSeaLightsListenerWhenWithNameWithoutValue() throws Exception {
        performTest("12_Insert_Sealights_listener_when_with_name_without_value");
    }

    @Test
    public void insertSeaLightsListenerWhenWithoutNameWithoutValue() throws Exception {
        performTest("13_Insert_Sealights_listener_when_without_name_without_value");
    }

    @Test
    public void insertSeaLightsListenerWhenWithNameWithDifferentValue() throws Exception {
        performTest("14_Insert_Sealights_listener_when_with_name_with_value_different_from_ours");
    }

    @Test
    public void dontInjectSeaLightsAdditionalClassPathElementWhenAlreadyExist() throws Exception {
        performTest("15_dont_Insert_Sealights_additionalClassPathElement_when_already_exist");
    }

    @Test
    public void injectSeaLightsWhenBuildElementNotExist() throws Exception {
        performTest("16_Inject_SeaLights_when_build_element_not_exist");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjected() throws Exception {
        performTest("17_Dont_inject_Sealights_plugin_if_already_injected");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInPluginManagement() throws Exception {
        performTest("18_Dont_inject_Sealights_plugin_if_already_injected_in_pluginManagement");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInPlugins() throws Exception {
        performTest("19_Dont_inject_Sealights_plugin_if_already_injected_in_plugins");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInProfile() throws Exception {
        performTest("20_Dont_inject_Sealights_plugin_if_already_injected_in_profile");
    }

    @Test()
    public void ifNameTagIsNotListenerAddNameTagWithOurListener() throws Exception {
        performTest("21_If_name_tag_is_not_listener_add_name_tag_with_our_listener");
    }

    @Test
    public void injectSeaLightsPluginWithJunit3ListenerToAPomWithoutThePluginButWithSurefire() throws Exception {
        performTest("22_Inject_SeaLights_plugin_with_junit3_listener_to_a_pom_without_the_plugin_but_with_surefire", TestingFramework.JUNIT_3);
    }

    private void performTest(String testCase) throws Exception {
        performTest(testCase, TestingFramework.JUNIT_4);
    }

    private void performTest(String testCase, TestingFramework testingFramework) throws Exception {
        //Arrange
        TestingFramework TESTING_FRAMEWORK= testingFramework;
        String TEST_CASE = testCase;
        String testFolder = getTestFolder(TEST_CASE);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        mavenIntegrationInfo.setTestingFramework(TESTING_FRAMEWORK);
        MavenIntegration mavenIntegration = new MavenIntegration(new Logger(new PrintStream(System.out)),mavenIntegrationInfo, SAVE_POM_USING_JENKINS_API);

        //Act
        mavenIntegration.integrate(false);

        //Assert
        String expected = readFile(testFolder + "/expected.xml");
        String actual = readFile(testFolder + "/actual.xml");

        assertXMLEquals(expected, actual);
    }

    public static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));

        List<?> allDifferences = diff.getAllDifferences();
        Assert.assertEquals("Differences found: "+ diff.toString(), 0, allDifferences.size());
    }


    private String readFile(String filepath) throws IOException {
        String s = FileUtils.readFileToString(new File(filepath));
        return s;
    }



    private MavenIntegrationInfo createDefaultMavenIntegrationInfo(String path){


        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setEnabled(true);
        slInfo.setBuildName("1");
        slInfo.setCustomerId("fake-customer-id-123");
        slInfo.setServerUrl("http://fake-server-url.com");

        slInfo.setWorkspacepath("c:\\fake-worakpsacepath");
        slInfo.setBuildFilesFolders(path);
        slInfo.setExecutionType(ExecutionType.FULL);

        slInfo.setAppName("fake-app-name");
        slInfo.setModuleName("fake-module-name");
        slInfo.setBranchName("fake-branch");
        slInfo.setFilesIncluded("*.class");
        slInfo.setRecursive(true);
        slInfo.setPackagesIncluded("com.fake.*");
        slInfo.setPackagesExcluded("com.fake.excluded.*");
        slInfo.setBuildFilesPatterns("*pom.xml");

        slInfo.setListenerJar("c:\\fake-test-listener.jar");
        slInfo.setScannerJar("c:\\fake-build-scanner.jar");
        slInfo.setApiJar("c:\\sl-api.jar");
        slInfo.setBuildStrategy(BuildStrategy.ONE_BUILD);

        slInfo.setLogEnabled(false);
        slInfo.setLogLevel(LogLevel.INFO);
        slInfo.setLogFolder("c:\\fake-log-folder");

        String source = path + "/pom.xml";
        String target = path + "/actual.xml";
        List<FileBackupInfo> files = new ArrayList<>();
        files.add(new FileBackupInfo(source, target));
        MavenIntegrationInfo info = new MavenIntegrationInfo(files, slInfo, TestingFramework.JUNIT_4);
        info.setTestingFramework(TestingFramework.TESTNG);
        info.setSeaLightsPluginInfo(slInfo);

        return info;
    }

    private String getTestFolder(String testCaseName)
    {
        return PATH + testCaseName;
    }
}