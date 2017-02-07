package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.model.AbstractBuild;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.CommandMode;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.ConfigCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by shahar on 2/5/2017.
 */
public class ConfigTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void execute_giveValidConfigArguments_shouldExecuteCorrectCommand() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        ConfigCommandArguments configArguments = createConfigArguments(baseCommandArguments);
        ConfigCommandExecutor configExecutor = new ConfigCommandExecutor(nullLogger, configArguments);
        configExecutor.setJenkinsUtils(createMockJenkinsUtils());

        Runtime runtimeMock = mock(Runtime.class);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        //Act
        configExecutor.setRuntime(runtimeMock);
        configExecutor.execute();
        verify(runtimeMock).exec(captor.capture());
        final String actualCommandLine = captor.getValue();
        String expectedCommandLine = "java -jar agent.jar -config -token \"fake-token\" -buildsessionidfile \"/path/to/buildsessionid.txt\" -appname \"demoApp\" -buildname \"1\" -branchname \"branchy\" -buildsessionidfile \"/path/to/workspace\\buildSessionId.txt\" -packagesincluded \"io.include.*\" -packagesexcluded \"io.exclude.*\" -enableNoneZeroErrorCode";

        // Assert
        Assert.assertEquals(
                "The command line that was executed for the 'start' executor is not as expected",
                expectedCommandLine, actualCommandLine);
    }

    private JenkinsUtils createMockJenkinsUtils() {
        JenkinsUtils jenkinsUtilsMock = mock(JenkinsUtils.class);
        when(jenkinsUtilsMock.getWorkspace((AbstractBuild<?, ?>) any(Object.class))).thenReturn("/path/to/workspace");
        return jenkinsUtilsMock;
    }

    @Test
    public void execute_runtimeProcessThrowsException_shouldEndQuietly() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        ConfigCommandArguments configArguments = createConfigArguments(baseCommandArguments);
        ConfigCommandExecutor configExecutor = new ConfigCommandExecutor(nullLogger, configArguments);

        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.exec(any(String.class))).thenThrow(new IOException());

        //Act
        configExecutor.setRuntime(runtimeMock);
        try {
            boolean result = configExecutor.execute();
            Assert.assertFalse("configExecutor.execute() should be false!", result);
        }catch (Exception e){
            Assert.fail("configExecutor.execute() should not throw exception!");
        }
    }

    private ConfigCommandArguments createConfigArguments(BaseCommandArguments baseCommandArguments) {
        ConfigCommandArguments configArguments = new ConfigCommandArguments(
                baseCommandArguments,
                "io.include.*", // packages included
                "io.exclude.*" // packages excluded
        );

        return configArguments;
    }

    private BaseCommandArguments createBaseCommandArguments(){
        BaseCommandArguments baseCommandArguments = new BaseCommandArguments();
        baseCommandArguments.setMode(createExternalReportViewCommandMode());
        baseCommandArguments.setAgentPath("agent.jar");
        baseCommandArguments.setToken("fake-token");
        baseCommandArguments.setAppName("demoApp");
        baseCommandArguments.setBuildName("1");
        baseCommandArguments.setBranchName("branchy");
        baseCommandArguments.setBuildSessionIdFile("/path/to/buildsessionid.txt");
        return baseCommandArguments;
    }

    private CommandMode.ExternalReportView createExternalReportViewCommandMode(){
        CommandMode.ExternalReportView externalReportView = new CommandMode.ExternalReportView();
        externalReportView.setReport("fakeReport.json");
        return externalReportView;
    }

}
