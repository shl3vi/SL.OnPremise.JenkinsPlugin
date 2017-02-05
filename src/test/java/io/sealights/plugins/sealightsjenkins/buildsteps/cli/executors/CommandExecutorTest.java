package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.cli.CommandMode;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.EndCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.StartCommandArguments;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.UploadReportsCommandArguments;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

public class CommandExecutorTest {

    private Logger nullLogger = new NullLogger();
    private String validToken = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1BOTkuYXV0aC5zZWFsaWdodHMuaW8vIiwiand0aWQiOiJERVYtQTk5LGktNTZiMDI0ZGQsdHhJZCwxNDc4NDUwNzUxODEyIiwic3ViamVjdCI6ImFnZW50c0BDdXN0b21lcklkIiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50Iiwic2xfaW1wZXJfc3ViamVjdCI6IiIsIngtc2wtc2VydmVyIjoiaHR0cDovL3d3dy5nb29nbGUuY29tL3NlYWxpZ2h0cyIsImlhdCI6MTQ3ODQ1MDc1M30.awtipSFsTcRCT6sUBWaFv2GKaXXZ7gCSBRXorar1nOpOkzUPQqPB9xz0rOOHY7Kb7vFnZUjsOOTob_ui2OZe430O7MJmdFkxrbpXQcUndvWHfi63STsGepI1q61tOejjrs7WiyInsUCMV00Tr25F2NRdf70PGloK8BBs9BdOhldJcEvTYnF8LPw5trAnE8YA-TuIxgjocR0a0QdF_JOibD2mpNQwIfvOsmNrrfArTOoZS2W1XZ_pXa-n1VuWDSgRZF9yVaPMwmqcLoNsydEURgtzuQj8cP5sUg6XjSLoAyfA6guTfZ4rIdJwxJ4GdC8k24yqzhV6X0c_mJ5yrlB9HNTBdIc651SrcMyd4UIM_-zMMEL-1ItKE-txdFijv9jeyr6mQxhbvkCeh6BRRJZqNti4dRrLeztAUbfsAayBEeTnAuMXXsMzSccS-pO0aU2zQMuZaVIzCHqIV9ex7vjwXNKGw4TspFkxw2w85QssHYvIUpPoQ7bzu8sFCKJY-phTRr7i6KCPBCez-Zlu_zL0txsZgwIcXE5rNZvRRC2imjrWVzGFb6IAGVHU3lbJuGocnl4Z-td1tf1mDZqZN9_NL8mltddUugeo7emJNU1UZiHN4lHEKxcayj4LFIgeTyE1R_d8EOi9WMieuEwpRB7r_qXMUDKci07su9UR6XpKS2I";

    @Test
    public void createExecutionCommand_startCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        String newEnvironment = "Integration";
        CommandMode mode = new CommandMode.StartView(newEnvironment);
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken(mode);
        StartCommandArguments startArguments = new StartCommandArguments(baseArguments, newEnvironment);
        BaseCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, startArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "java -jar /fake/path/to/agent.jar start -customerid \"fake-customer\" -server \"https://fake-url/api\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\" -testStage \"Integration\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_endCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        CommandMode mode = new CommandMode.EndView();
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken(mode);
        EndCommandArguments endArguments = new EndCommandArguments(baseArguments);
        BaseCommandExecutor commandExecutor = new EndCommandExecutor(nullLogger, endArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "java -jar /fake/path/to/agent.jar end -customerid \"fake-customer\" -server \"https://fake-url/api\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_uploadReportsCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        CommandMode mode = new CommandMode.UploadReportsView(null, null, false, null);
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken(mode);
        UploadReportsCommandArguments uploadReportsArguments = new UploadReportsCommandArguments(baseArguments, null, null, false, null);
        BaseCommandExecutor commandExecutor = new UploadReportsCommandExecutor(nullLogger, uploadReportsArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "java -jar /fake/path/to/agent.jar uploadReports -customerid \"fake-customer\" -server \"https://fake-url/api\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\" -hasMoreRequests \"false\" -source \"null\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_setJavaPath_withoutToken_shouldUseGivenJavaPath() {
        //Arrange
        String newEnvironment = "Integration";
        CommandMode mode = new CommandMode.StartView(newEnvironment);
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken(mode, "path/to/java");
        StartCommandArguments startArguments = new StartCommandArguments(baseArguments, newEnvironment);
        BaseCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, startArguments);
        //Act
        String actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String expectedCommand = "path/to/java -jar /fake/path/to/agent.jar start -customerid \"fake-customer\" -server \"https://fake-url/api\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\" -testStage \"Integration\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }


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
        String expectedCommand = "java -jar /fake/path/to/agent.jar start -token \"" + validToken + "\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\" -testStage \"Integration\"";
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
        String expectedCommand = "java -jar /fake/path/to/agent.jar end -token \"" + validToken + "\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\"";
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
        String expectedCommand = "java -jar /fake/path/to/agent.jar uploadReports -token \"" + validToken + "\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\" -hasMoreRequests \"false\" -source \"null\"";
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
        String expectedCommand = "path/to/java -jar /fake/path/to/agent.jar start -token \"" + validToken + "\" -appname \"fake-app\" -buildname \"fake-build\" -branchname \"fake-branch\" -environment \"fake-env\" -testStage \"Integration\"";
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    private BaseCommandArguments createBaseCommandArguments(CommandMode mode, String javaPath) {
        BaseCommandArguments baseArgs = createBaseCommandArguments(mode);
        baseArgs.setJavaPath(javaPath);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArgumentsWithoutToken(CommandMode mode, String javaPath) {
        BaseCommandArguments baseArgs = createBaseCommandArguments(mode, javaPath);
        baseArgs.setTokenData(null);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArgumentsWithoutToken(CommandMode mode) {
        BaseCommandArguments baseArgs = createBaseCommandArguments(mode);
        baseArgs.setTokenData(null);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArguments(CommandMode mode) {
        BaseCommandArguments baseArgs = new BaseCommandArguments();

        TokenData tokenData = new TokenData();
        tokenData.setToken(validToken);

        baseArgs.setMode(mode);
        baseArgs.setTokenData(tokenData);
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
