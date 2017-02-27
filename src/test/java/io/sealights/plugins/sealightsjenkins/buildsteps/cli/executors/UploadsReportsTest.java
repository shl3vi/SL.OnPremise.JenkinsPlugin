package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.EnvVars;
import io.sealights.plugins.sealightsjenkins.TestHelper;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.UploadReportsCommandArguments;
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
public class UploadsReportsTest {

    private TestHelper testHelper = new TestHelper();
    private Logger nullLogger = new NullLogger();
    private boolean NO_MORE_REQUESTS = false;

    @Test
    public void execute_giveValidUploadReportsArguments_shouldExecuteCorrectCommand() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        UploadReportsCommandArguments uploadReportsArguments =
                new UploadReportsCommandArguments("report1.txt,report2.txt", "folders", NO_MORE_REQUESTS, "someSource");
        UploadReportsCommandExecutor uploadReportsExecutor = new UploadReportsCommandExecutor(nullLogger, baseCommandArguments, uploadReportsArguments);

        Runtime runtimeMock = mock(Runtime.class);

        final ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);

        //Act
        uploadReportsExecutor.setRuntime(runtimeMock);
        uploadReportsExecutor.execute();
        verify(runtimeMock).exec(captor.capture());
        final String[] actualCommandLine = captor.getValue();
        String[] expectedCommandLine = {"path/to/java", "-jar", "agent.jar", "uploadReports", "-token", "fake-token", "-buildsessionidfile", "/path/to/buildsessionid.txt", "-appname", "demoApp", "-buildname", "1", "-branchname", "branchy", "-labid", "someEnv", "-reportFile", "report1.txt", "-reportFile", "report2.txt", "-reportFilesFolder", "folders", "-hasMoreRequests", "false", "-source", "someSource"};

        // Assert
        Assert.assertArrayEquals(
                "The command line that was executed for the 'upload reports' executor is not as expected",
                expectedCommandLine, actualCommandLine);
    }

    @Test
    public void execute_runtimeProcessThrowsException_shouldEndQuietly() throws IOException {
        //Arrange
        BaseCommandArguments baseCommandArguments = createBaseCommandArguments();
        UploadReportsCommandArguments uploadReportsArguments =
                new UploadReportsCommandArguments("report1.txt,report2.txt", "folders", NO_MORE_REQUESTS, "someSource");
        UploadReportsCommandExecutor uploadReportsExecutor = new UploadReportsCommandExecutor(nullLogger, baseCommandArguments, uploadReportsArguments);

        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.exec(any(String.class))).thenThrow(new IOException());

        //Act
        uploadReportsExecutor.setRuntime(runtimeMock);
        try {
            boolean result = uploadReportsExecutor.execute();
            Assert.assertFalse("uploadReportsExecutor.execute() should be false!", result);
        }catch (Exception e){
            Assert.fail("uploadReportsExecutor.execute() should not throw exception!");
        }
    }

    private BaseCommandArguments createBaseCommandArguments() throws IOException {
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
        TestHelper.BuildMock build = testHelper.createBuildMock();
        baseCommandArguments.setBuild(build);

        return baseCommandArguments;
    }

}
