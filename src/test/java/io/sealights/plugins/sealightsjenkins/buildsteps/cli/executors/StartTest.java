package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.EnvVars;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.StartCommandArguments;
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
public class StartTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void execute_giveValidStartArguments_shouldExecuteCorrectCommand() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        StartCommandArguments startArguments = new StartCommandArguments("newEnv");
        StartCommandExecutor startExecutor = new StartCommandExecutor(nullLogger, baseCommandArguments, startArguments);

        Runtime runtimeMock = mock(Runtime.class);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        //Act
        startExecutor.setRuntime(runtimeMock);
        startExecutor.execute();
        verify(runtimeMock).exec(captor.capture());
        final String actualCommandLine = captor.getValue();
        String expectedCommandLine = "path/to/java -jar agent.jar start -token \"fake-token\" -buildsessionidfile \"/path/to/buildsessionid.txt\" -appname \"demoApp\" -buildname \"1\" -branchname \"branchy\" -labid \"someEnv\" -testStage \"newEnv\"";

        // Assert
        Assert.assertEquals(
                "The command line that was executed for the 'start' executor is not as expected",
                expectedCommandLine, actualCommandLine);
    }

    @Test
    public void execute_runtimeProcessThrowsException_shouldEndQuietly() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        StartCommandArguments startArguments = new StartCommandArguments("newEnv");
        StartCommandExecutor startExecutor = new StartCommandExecutor(nullLogger, baseCommandArguments, startArguments);

        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.exec(any(String.class))).thenThrow(new IOException());

        //Act
        startExecutor.setRuntime(runtimeMock);
        try {
            boolean result = startExecutor.execute();
            Assert.assertFalse("startExecutor.execute() should be false!", result);
        }catch (Exception e){
            Assert.fail("startExecutor.execute() should not throw exception!");
        }
    }

    private BaseCommandArguments createBaseCommandArguments(){
        BaseCommandArguments baseCommandArguments = new BaseCommandArguments();
        baseCommandArguments.setJavaPath("path/to/java");
        baseCommandArguments.setAgentPath("agent.jar");
        baseCommandArguments.setToken("fake-token");
        baseCommandArguments.setAppName("demoApp");
        baseCommandArguments.setBuildName("1");
        baseCommandArguments.setBranchName("branchy");
        baseCommandArguments.setBuildSessionIdFile("/path/to/buildsessionid.txt");
        baseCommandArguments.setLabId("someEnv");
        baseCommandArguments.setEnvVars(new EnvVars());
        return baseCommandArguments;
    }

}
