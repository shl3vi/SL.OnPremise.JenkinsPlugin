package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.CommandMode;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.EndCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.StartCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.UploadReportsCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

public class CommandExecutorTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void createExecutionCommand_startCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        String newEnvironment = "Integration";
        CommandMode mode = new CommandMode.StartView(newEnvironment);
        BaseCommandArguments baseArguments = createBaseCommandArguments(mode);
        StartCommandArguments startArguments = new StartCommandArguments(baseArguments, newEnvironment);
        BaseCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, startArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "java -jar /fake/path/to/agent.jar start -customerid \"fake-customer\" -appname \"fake-app\" -branchname \"fake-branch\" -environment \"fake-env\" -server \"https://fake-url/api\" -buildname \"fake-build\" -testPhase \"Integration\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_endCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        CommandMode mode = new CommandMode.EndView();
        BaseCommandArguments baseArguments = createBaseCommandArguments(mode);
        EndCommandArguments endArguments = new EndCommandArguments(baseArguments);
        BaseCommandExecutor commandExecutor = new EndCommandExecutor(nullLogger, endArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "java -jar /fake/path/to/agent.jar end -customerid \"fake-customer\" -appname \"fake-app\" -branchname \"fake-branch\" -environment \"fake-env\" -server \"https://fake-url/api\" -buildname \"fake-build\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_uploadReportsCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        CommandMode mode = new CommandMode.UploadReportsView(null, null, false, null);
        BaseCommandArguments baseArguments = createBaseCommandArguments(mode);
        UploadReportsCommandArguments uploadReportsArguments = new UploadReportsCommandArguments(baseArguments, null, null, false, null);
        BaseCommandExecutor commandExecutor = new UploadReportsCommandExecutor(nullLogger, uploadReportsArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "java -jar /fake/path/to/agent.jar uploadReports -customerid \"fake-customer\" -appname \"fake-app\" -branchname \"fake-branch\" -environment \"fake-env\" -server \"https://fake-url/api\" -buildname \"fake-build\" -hasMoreRequests \"false\" -source \"null\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_setJavaPath_shouldUseGivenJavaPath() {
        //Arrange
        String newEnvironment = "Integration";
        CommandMode mode = new CommandMode.StartView(newEnvironment);
        BaseCommandArguments baseArguments = createBaseCommandArguments(mode, "path/to/java");
        StartCommandArguments startArguments = new StartCommandArguments(baseArguments, newEnvironment);
        BaseCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, startArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "path/to/java -jar /fake/path/to/agent.jar start -customerid \"fake-customer\" -appname \"fake-app\" -branchname \"fake-branch\" -environment \"fake-env\" -server \"https://fake-url/api\" -buildname \"fake-build\" -testPhase \"Integration\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    private BaseCommandArguments createBaseCommandArguments(CommandMode mode, String javaPath){
        BaseCommandArguments baseArgs = createBaseCommandArguments(mode);
        baseArgs.setJavaPath(javaPath);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArguments(CommandMode mode){
        BaseCommandArguments baseArgs = new BaseCommandArguments();

        baseArgs.setMode(mode);
        baseArgs.setAgentPath("/fake/path/to/agent.jar");
        baseArgs.setAppName("fake-app");
        baseArgs.setBuildName("fake-build");
        baseArgs.setBranchName("fake-branch");
        baseArgs.setEnvironment("fake-env");
        baseArgs.setCustomerId("fake-customer");
        baseArgs.setUrl("https://fake-url/api");
        baseArgs.setProxy(null);

        return baseArgs;
    }
}
