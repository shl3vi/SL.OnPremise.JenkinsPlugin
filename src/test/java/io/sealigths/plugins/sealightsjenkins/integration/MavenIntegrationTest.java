package io.sealigths.plugins.sealightsjenkins.integration;

import io.sealigths.plugins.sealightsjenkins.BuildStrategy;
import io.sealigths.plugins.sealightsjenkins.ExecutionType;
import io.sealigths.plugins.sealightsjenkins.LogLevel;
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

    private String PATH = System.getProperty("user.dir") + "/src/test/cases/MavenIntegration/";

    @Test
    public void injectSeaLightsPluginToAPomWithoutThePlugin() throws Exception {
        performTest("1_Inject_SeaLights_plugin_to_a_pom_without_the_plugin");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithSingleProfile() throws Exception {
        performTest("2_Inject_SeaLights_plugin_to_a_pom_with_a_single_profile");
    }

    @Test
    public void injectSeaLightsPluginToAPomWithTwoProfiles() throws Exception {
        performTest("3_Inject_SeaLights_plugin_to_a_pom_with_a_two_profiles");
    }

    @Test
    public void injectSeaLightsWhenBuildElementNotExist() throws Exception {
        performTest("4_Inject_SeaLights_when_build_element_not_exist");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjected() throws Exception {
        performTest("5_Dont_inject_Sealights_plugin_if_already_injected");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInPluginManagement() throws Exception {
        performTest("6_Dont_inject_Sealights_plugin_if_already_injected_in_pluginManagement");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInPlugins() throws Exception {
        performTest("7_Dont_inject_Sealights_plugin_if_already_injected_in_plugins");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInProfile() throws Exception {
        performTest("8_Dont_inject_Sealights_plugin_if_already_injected_in_profile");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontIntegrateIfUnsupportedForkModeNeverPresent() throws Exception {
        performTest("9_Dont_integrate_if_unsupported_forkMode_never_present");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontIntegrateIfUnsupportedForkModePerthreadWithoutThreadCountPresent() throws Exception {
        performTest("10_Dont_integrate_if_unsupported_forkMode_perthread_without_threadCount_present");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontIntegrateIfUnsupportedForkModePerthreadWithThreadCount0Present() throws Exception {
        performTest("11_Dont_integrate_if_unsupported_forkMode_perthread_with_threadCount_0_present");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontIntegrateIfUnsupportedForkCountPresent() throws Exception {
        performTest("12_Dont_integrate_if_unsupported_forkCount_present");
    }

    @Test(expected = FileNotFoundException.class)
    public void dontIntegrateIfUnsupportedParallelPresent() throws Exception {
        performTest("13_Dont_integrate_if_unsupported_parallel_present");
    }

    @Test
    public void injectSeaLightsPluginToPomWith_surefire_that_has_argLine_element_that_doesnt_chain_old_values() throws Exception {
        performTest("14_Inject_SeaLights_plugin_to_pom_with_surefire_that_has_argLine_element_that_doesnt_chain_old_values");
    }

    @Test
    public void injectSeaLightsPluginToPomWith_surefire_that_has_argLine_element_that_chain_old_values() throws Exception {
        performTest("15_Inject_SeaLights_plugin_to_pom_with_surefire_that_has_argLine_element_that_chain_old_values");
    }

    private void performTest(String testCase) throws Exception {
        //Arrange
        final boolean SAVE_POM_USING_JENKINS_API = false;
        String testFolder = getTestFolder(testCase);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder);
        MavenIntegration mavenIntegration = new MavenIntegration(new Logger(new PrintStream(System.out)),mavenIntegrationInfo, SAVE_POM_USING_JENKINS_API);

        //Act
        mavenIntegration.integrate(false);

        //Assert
        String expected = readFile(testFolder + "/expected.xml");
        String actual = readFile(testFolder + "/actual.xml");

        assertXMLEquals(expected, actual);
    }

    private static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
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
        slInfo.setBuildStrategy(BuildStrategy.ONE_BUILD);

        slInfo.setLogEnabled(false);
        slInfo.setLogLevel(LogLevel.INFO);
        slInfo.setLogFolder("c:\\fake-log-folder");

        String source = path + "/pom.xml";
        String target = path + "/actual.xml";
        List<FileBackupInfo> files = new ArrayList<>();
        files.add(new FileBackupInfo(source, target));
        MavenIntegrationInfo info = new MavenIntegrationInfo(files, slInfo);
        info.setSeaLightsPluginInfo(slInfo);

        return info;
    }

    private String getTestFolder(String testCaseName)
    {
        return PATH + testCaseName;
    }
}