package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.BuildStrategy;
import io.sealights.plugins.sealightsjenkins.ExecutionType;
import io.sealights.plugins.sealightsjenkins.LogLevel;
import io.sealights.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.PathUtils;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MavenIntegrationTest {

    private final static boolean SHOULD_INTEGRATE = true;
    private final static boolean SHOULD_NOT_INTEGRATE = false;
    private String PATH = PathUtils.join(System.getProperty("user.dir"), "src", "test", "cases", "MavenIntegration");

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

    @Test
    public void dontInjectSeaLightsPluginIfAlreadyInjected() throws Exception {
        performTest("5_Dont_inject_Sealights_plugin_if_already_injected", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInPluginManagement() throws Exception {
        performTest("6_Dont_inject_Sealights_plugin_if_already_injected_in_pluginManagement", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInPlugins() throws Exception {
        performTest("7_Dont_inject_Sealights_plugin_if_already_injected_in_plugins", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void dontInjectSeaLightsPluginIfAlreadyInjectedInProfile() throws Exception {
        performTest("8_Dont_inject_Sealights_plugin_if_already_injected_in_profile", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void dontIntegrateIfUnsupportedForkModeNeverPresent() throws Exception {
        performTest("9_Dont_integrate_if_unsupported_forkMode_never_present", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void injectSeaLightsIfSupportedForkModeOptionPresent() throws Exception {
        performTest("10_Inject_SeaLights_if_supported_forkMode_option_present");
    }

    @Test
    public void dontIntegrateIfUnsupportedForkModePerthreadWithoutThreadCountPresent() throws Exception {
        performTest("11_Dont_integrate_if_unsupported_forkMode_perthread_without_threadCount_present", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void dontIntegrateIfUnsupportedForkModePerthreadWithThreadCount0Present() throws Exception {
        performTest("12_Dont_integrate_if_unsupported_forkMode_perthread_with_threadCount_0_present", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void injectSeaLightsIfExistForkModePerthreadWithThreadCountGreaterThan0Present() throws Exception {
        performTest("13_Inject_SeaLights_if_exist_forkMode_perthread_with_threadCount_greater_than_0_present");
    }

    @Test
    public void dontIntegrateIfUnsupportedForkCountPresent() throws Exception {
        performTest("14_Dont_integrate_if_unsupported_forkCount_present", SHOULD_NOT_INTEGRATE);
    }

    @Test
    public void injectSeaLightsIfForkCountGreaterThan0() throws Exception {
        performTest("15_Inject_SeaLights_if_forkCount_greater_than_0");
    }

    @Test
    public void injectSealightsIfUnsupportedParallelPresent() throws Exception {
        performTest("16_Inject_Sealights_if_unsupported_parallel_present");
    }

    @Test
    public void injectSeaLightsPluginToPomWith_surefire_that_has_argLine_element_that_doesnt_chain_old_values() throws Exception {
        performTest("17_Inject_SeaLights_plugin_to_pom_with_surefire_that_has_argLine_element_that_doesnt_chain_old_values");
    }

    @Test
    public void injectSeaLightsPluginToPomWith_surefire_that_has_argLine_element_that_chain_old_values() throws Exception {
        performTest("18_Inject_SeaLights_plugin_to_pom_with_surefire_that_has_argLine_element_that_chain_old_values");
    }

    @Test
    public void injectSeaLightsPluginWithSpecificVersionToAPomWithoutThePlugin() throws Exception {
        performTest("19_Inject_SeaLights_plugin_with_specific_version_to_a_pom_without_the_plugin", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void injectSeaLightsArgumentsToJMeterPluginWhenArgumentsElementExists() throws Exception {
        performTest("20_Inject_SeaLights_arguments_to_jMeter_plugin_when_arguments_element_exists", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void dontInjectSeaLightsArgumentsToJMeterPluginWhenOurArgumentsAlreadyPresent() throws Exception {
        performTest("21_Dont_inject_SeaLights_arguments_to_jMeter_plugin_when_our_arguments_already_present", SHOULD_NOT_INTEGRATE, "1.1.1");
    }

    @Test
    public void injectSeaLightsArgumentsToJMeterPluginWhenNonSealightsArgumentsExists() throws Exception {
        performTest("22_Inject_SeaLights_arguments_to_jMeter_plugin_when_non-Sealights_arguments_exists", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void injectSeaLightsArgumentsToJMeterPluginWhenArgumentsElementNotExists() throws Exception {
        performTest("23_Inject_SeaLights_arguments_to_jMeter_plugin_when_arguments_element_not_exists", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void InjectSeaLightsArgumentsToJMeterPluginInsideProfile() throws Exception {
        performTest("24_Inject_SeaLights_arguments_to_jMeter_plugin_inside_profile", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void InjectSeaLightsArgumentsToJMeterPluginWhenOnlyJMeterProcessJVMSettingsExists() throws Exception {
        performTest("25_Inject_SeaLights_arguments_to_jMeter_plugin_when_only_jMeterProcessJVMSettings_exists", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void InjectSeaLightsArgumentsToJMeterPluginWhenWithJMeterProcessJVMSettingsExistsWithElementOtherThanArguments() throws Exception {
        performTest("26_Inject_SeaLights_arguments_to_jMeter_plugin_when_with_jMeterProcessJVMSettings_exists_with_element_other_than_element", SHOULD_INTEGRATE, "1.1.1");
    }

    @Test
    public void dontInject_SeaLights_arguments_to_jMeter_plugin_when_property_skipJMeter_present() throws Exception {
        performTest("27_Dont_inject_SeaLights_arguments_to_jMeter_plugin_when_property_skipJMeter_present", SHOULD_INTEGRATE, "1.1.1");
    }

    private void performTest(String testCase) throws Exception {
        performTest(testCase, SHOULD_INTEGRATE, null);
    }

    private void performTest(String testCase, boolean shouldFindActual) throws Exception {
        performTest(testCase, shouldFindActual, null);
    }

    private void performTest(String testCase, boolean shouldFindActual, String specificVersion) throws Exception {
        //Arrange
        final boolean SAVE_POM_USING_JENKINS_API = false;
        String testFolder = getTestFolder(testCase);

        MavenIntegrationInfo mavenIntegrationInfo = createDefaultMavenIntegrationInfo(testFolder, specificVersion);
        MavenIntegration mavenIntegration = new MavenIntegrationMock(new Logger(new PrintStream(System.err)), mavenIntegrationInfo, SAVE_POM_USING_JENKINS_API);

        //Act
        mavenIntegration.integrate(false);

        String expectedFileName = PathUtils.join(testFolder, "expected.xml");
        String actualFileName = PathUtils.join(testFolder, "actual.xml");

        if (shouldFindActual) {
            //Assert
            String expected = readFile(expectedFileName);
            String actual = readFile(actualFileName);

            assertXMLEquals(expected, actual);
            deleteActualPom(actualFileName);

        } else {
            File actual = new File(actualFileName);
            Assert.assertFalse("'actual.xml' should not have been created as we should not modify the pom.", actual.exists());
        }
    }

    private void deleteActualPom(String filePath) {
        try {
            File fileToDelete = new File(filePath);
            boolean deleted = fileToDelete.delete();
            if (!deleted) {
                System.out.println("Failed while trying to delete '" + filePath + "' file. Please delete manually");
            }
        } catch (Exception e) {
            System.out.println("Failed while trying to delete '" + filePath + "' file. Please delete manually");
        }
    }

    private static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));

        List<?> allDifferences = diff.getAllDifferences();
        Assert.assertEquals("Differences found: " + diff.toString(), 0, allDifferences.size());
    }


    private String readFile(String filepath) throws IOException {
        String s = FileUtils.readFileToString(new File(filepath));
        return s;
    }


    private MavenIntegrationInfo createDefaultMavenIntegrationInfo(String path, String specificVersion) {


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

        Map<String, String> metadata = new HashMap<>();
        metadata.put("build", "someBuildInfo");
        metadata.put("plugin", "somPluginInfo");
        slInfo.setMetadata(metadata);

        String source = path + "/pom.xml";
        String target = path + "/actual.xml";
        List<FileBackupInfo> files = new ArrayList<>();
        files.add(new FileBackupInfo(source, target));
        MavenIntegrationInfo info = new MavenIntegrationInfo(files, slInfo, specificVersion);
        info.setSeaLightsPluginInfo(slInfo);

        return info;
    }

    private String getTestFolder(String testCaseName) {
        return PathUtils.join(PATH, testCaseName);
    }

    private class MavenIntegrationMock extends MavenIntegration{

        public MavenIntegrationMock(Logger log, MavenIntegrationInfo mavenIntegrationInfo, boolean isJenkinsEnvironment) {
            super(log, mavenIntegrationInfo, isJenkinsEnvironment);
        }

        @Override
        protected String createOverrideTestListenerPath(){
            return "/path/to/override-sl-test-listener.jar";
        }

        @Override
        protected String createOverrideMetaJsonPath(){
            return "/path/to/override-mata.json";
        }
    }
}