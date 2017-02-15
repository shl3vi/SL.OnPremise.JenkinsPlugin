package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.EndCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by shahar on 2/5/2017.
 */
public class EndTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void execute_giveValidEndArguments_shouldExecuteCorrectCommand() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        EndCommandArguments endArguments = new EndCommandArguments();
        EndCommandExecutor endExecutor = new EndCommandExecutor(nullLogger, baseCommandArguments, endArguments);

        Runtime runtimeMock = mock(Runtime.class);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        //Act
        endExecutor.setRuntime(runtimeMock);
        endExecutor.execute();
        verify(runtimeMock).exec(captor.capture());
        final String actualCommandLine = captor.getValue();
        String expectedCommandLine = "path/to/java -jar agent.jar end -token \"fake-token\" -buildsessionidfile \"/path/to/buildsessionid.txt\" -appname \"demoApp\" -buildname \"1\" -branchname \"branchy\" -labid \"someEnv\"";

        // Assert
        Assert.assertEquals(
                "The command line that was executed for the 'end' executor is not as expected",
                expectedCommandLine, actualCommandLine);
    }

    @Test
    public void execute_runtimeProcessThrowsException_shouldEndQuietly() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        EndCommandArguments endArguments = new EndCommandArguments();
        EndCommandExecutor endExecutor = new EndCommandExecutor(nullLogger, baseCommandArguments, endArguments);

        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.exec(any(String.class))).thenThrow(new IOException());

        //Act
        endExecutor.setRuntime(runtimeMock);
        try {
            boolean result = endExecutor.execute();
            Assert.assertFalse("endExecutor.execute() should be false!", result);
        }catch (Exception e){
            Assert.fail("endExecutor.execute() should not throw exception!");
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
        return baseCommandArguments;
    }

}
