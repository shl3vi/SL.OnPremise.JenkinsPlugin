package io.sealights.plugins.sealightsjenkins.buildsteps.cli;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import io.sealights.plugins.sealightsjenkins.BeginAnalysis;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CLIRunnerTest {

    private Logger nullLogger = new NullLogger();
    private String validToken = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1BOTkuYXV0aC5zZWFsaWdodHMuaW8vIiwiand0aWQiOiJERVYtQTk5LGktNTZiMDI0ZGQsdHhJZCwxNDc4NDUwNzUxODEyIiwic3ViamVjdCI6ImFnZW50c0BDdXN0b21lcklkIiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50Iiwic2xfaW1wZXJfc3ViamVjdCI6IiIsIngtc2wtc2VydmVyIjoiaHR0cDovL3d3dy5nb29nbGUuY29tL3NlYWxpZ2h0cyIsImlhdCI6MTQ3ODQ1MDc1M30.awtipSFsTcRCT6sUBWaFv2GKaXXZ7gCSBRXorar1nOpOkzUPQqPB9xz0rOOHY7Kb7vFnZUjsOOTob_ui2OZe430O7MJmdFkxrbpXQcUndvWHfi63STsGepI1q61tOejjrs7WiyInsUCMV00Tr25F2NRdf70PGloK8BBs9BdOhldJcEvTYnF8LPw5trAnE8YA-TuIxgjocR0a0QdF_JOibD2mpNQwIfvOsmNrrfArTOoZS2W1XZ_pXa-n1VuWDSgRZF9yVaPMwmqcLoNsydEURgtzuQj8cP5sUg6XjSLoAyfA6guTfZ4rIdJwxJ4GdC8k24yqzhV6X0c_mJ5yrlB9HNTBdIc651SrcMyd4UIM_-zMMEL-1ItKE-txdFijv9jeyr6mQxhbvkCeh6BRRJZqNti4dRrLeztAUbfsAayBEeTnAuMXXsMzSccS-pO0aU2zQMuZaVIzCHqIV9ex7vjwXNKGw4TspFkxw2w85QssHYvIUpPoQ7bzu8sFCKJY-phTRr7i6KCPBCez-Zlu_zL0txsZgwIcXE5rNZvRRC2imjrWVzGFb6IAGVHU3lbJuGocnl4Z-td1tf1mDZqZN9_NL8mltddUugeo7emJNU1UZiHN4lHEKxcayj4LFIgeTyE1R_d8EOi9WMieuEwpRB7r_qXMUDKci07su9UR6XpKS2I";

    @Test
    public void perform_overrideTokenFromAdditionalArgs_useInvalidToken_executionShouldReturnFalse() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "token=invalid-token";

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = new CLIHandlerMock(nullLogger);
        AbstractBuild<?, ?> build = createJenkinsBuildObject(new EnvVars());
        BuildListener listener = mock(BuildListener.class);
        CommandMode commandMode = new CommandMode.EndView();

        //Act
        boolean successfulExecution = cliRunner.perform(build, null, listener, commandMode, cliHandler, nullLogger);

        //Assert
        Assert.assertFalse("Execution should return 'false' when providing invalid token", successfulExecution);
    }

    @Test
    public void perform_overrideTokenWithInvalidToken_shouldPassCustomerIdAndServer() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "token=invalid-token\ncustomerid=fake-customer\nserver=fake-server";

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        TokenData actualToken = cliHandler.getBaseArgs().getTokenData();
        String actualCustomerId = cliHandler.getBaseArgs().getCustomerId();
        String actualServer = cliHandler.getBaseArgs().getUrl();

        Assert.assertNull("token should be 'null' as the provided token is invalid", actualToken);
        Assert.assertNotNull("customerId should be passed due to invalid token", actualCustomerId);
        Assert.assertNotNull("server should be passed due to invalid token", actualServer);
    }

    @Test
    public void perform_overrideTokenWithValidToken_shouldNotPassCustomerIdAndServer() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "token=" + validToken + "\ncustomerid=fake-customer\nserver=fake-server";

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedToken = validToken;
        String actualToken = cliHandler.getBaseArgs().getTokenData().getToken();
        String actualCustomerId = cliHandler.getBaseArgs().getCustomerId();
        String actualServer = cliHandler.getBaseArgs().getUrl();

        Assert.assertEquals("token should be override by the additional arguments", expectedToken, actualToken);
        Assert.assertNull("customerId should be 'null' because valid token has been provided", actualCustomerId);
        Assert.assertNull("server should be 'null' because valid token has been provided", actualServer);
    }

    @Test
    public void perform_overrideTokenFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "token=" + validToken;

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedToken = validToken;
        String actualToken = cliHandler.getBaseArgs().getTokenData().getToken();

        Assert.assertEquals("token should be override by the additional arguments", expectedToken, actualToken);
    }

    @Test
    public void perform_overrideTokenWithEnvVar_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "token=${SL_TOKEN}";
        EnvVars envVars = new EnvVars();
        envVars.put("SL_TOKEN", validToken);

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        BeginAnalysis.DescriptorImpl globalConfiguration = cliRunner.getBeginAnalysis().getDescriptor();
        globalConfiguration.setToken("some-global-token");
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedToken = validToken;
        String actualToken = cliHandler.getBaseArgs().getTokenData().getToken();

        Assert.assertEquals("token should be override by the additional arguments and as environment variable", expectedToken, actualToken);
    }

    @Test
    public void perform_overrideCustomerIdFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "customerid=fake-customer";

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        BeginAnalysis.DescriptorImpl globalConfiguration = cliRunner.getBeginAnalysis().getDescriptor();
        globalConfiguration.setUrl("url");
        globalConfiguration.setCustomerId("customer");
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedCustomerId = "fake-customer";
        String actualCustomerId = cliHandler.getBaseArgs().getCustomerId();

        Assert.assertEquals("customerid should be override by the additional arguments", expectedCustomerId, actualCustomerId);
    }

    @Test
    public void perform_overrideCustomerIdWithEnvVar_shouldResolveAndOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "customerid=${TEST_CUSTOMER}";
        EnvVars envVars = new EnvVars();
        envVars.put("TEST_CUSTOMER", "fake-customer");

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        BeginAnalysis.DescriptorImpl globalConfiguration = cliRunner.getBeginAnalysis().getDescriptor();
        globalConfiguration.setUrl("url");
        globalConfiguration.setCustomerId("customer");
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedCustomerId = "fake-customer";
        String actualCustomerId = cliHandler.getBaseArgs().getCustomerId();

        Assert.assertEquals("customerid should be override by the additional arguments and as environment variable", expectedCustomerId, actualCustomerId);
    }

    @Test
    public void perform_overrideServerFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "server=fake-server";

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        BeginAnalysis.DescriptorImpl globalConfiguration = cliRunner.getBeginAnalysis().getDescriptor();
        globalConfiguration.setUrl("url");
        globalConfiguration.setCustomerId("customer");
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedServer = "fake-server";
        String actualServer = cliHandler.getBaseArgs().getUrl();

        Assert.assertEquals("server should be override by the additional arguments", expectedServer, actualServer);
    }

    @Test
    public void perform_overrideServerWithEnvVar_shouldResolveAndOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "server=${SERVER}";

        EnvVars envVars = new EnvVars();
        envVars.put("SERVER", "fake-server");

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        BeginAnalysis.DescriptorImpl globalConfiguration = cliRunner.getBeginAnalysis().getDescriptor();
        globalConfiguration.setUrl("url");
        globalConfiguration.setCustomerId("customer");
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedServer = "fake-server";
        String actualServer = cliHandler.getBaseArgs().getUrl();

        Assert.assertEquals("server should be override by the additional arguments and as environment variable", expectedServer, actualServer);
    }

    @Test
    public void perform_overrideProxyFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "proxy=fake-proxy";
        additionalArguments += "\ntoken=" + validToken;

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedProxy = "fake-proxy";
        String actualProxy = cliHandler.getBaseArgs().getProxy();

        Assert.assertEquals("proxy should be override by the additional arguments", expectedProxy, actualProxy);
    }

    @Test
    public void perform_overrideProxyWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "proxy=${PROXY}";
        additionalArguments += "\ntoken=" + validToken;

        EnvVars envVars = new EnvVars();
        envVars.put("PROXY", "fake-proxy");

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedProxy = "fake-proxy";
        String actualProxy = cliHandler.getBaseArgs().getProxy();

        Assert.assertEquals("proxy should be override by the additional arguments and as environment variable", expectedProxy, actualProxy);
    }

    @Test
    public void perform_overrideAgentPathFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "agentpath=fake-agent-path";
        additionalArguments += "\ntoken=" + validToken;

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedAgentPath = "fake-agent-path";
        String actualAgentPath = cliHandler.getBaseArgs().getAgentPath();

        Assert.assertEquals("agentpath should be override by the additional arguments", expectedAgentPath, actualAgentPath);
    }

    @Test
    public void perform_overrideAgentPathWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "agentpath=${AGENT_PATH}";
        additionalArguments += "\ntoken=" + validToken;

        EnvVars envVars = new EnvVars();
        envVars.put("AGENT_PATH", "fake-agent-path");

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedAgentPath = "fake-agent-path";
        String actualAgentPath = cliHandler.getBaseArgs().getAgentPath();

        Assert.assertEquals("agentpath should be override by the additional arguments and as environment variable", expectedAgentPath, actualAgentPath);
    }

    @Test
    public void perform_overrideJavaPathFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "javapath=fake-java-path";
        additionalArguments += "\ntoken=" + validToken;

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedJavaPath = "fake-java-path";
        String actualJavaPath = cliHandler.getBaseArgs().getJavaPath();

        Assert.assertEquals("javapath should be override by the additional arguments", expectedJavaPath, actualJavaPath);
    }

    @Test
    public void perform_overrideJavaPathWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "javapath=${JAVA_PATH}";
        additionalArguments += "\ntoken=" + validToken;

        EnvVars envVars = new EnvVars();
        envVars.put("JAVA_PATH", "fake-java-path");

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedJavaPath = "fake-java-path";
        String actualJavaPath = cliHandler.getBaseArgs().getJavaPath();

        Assert.assertEquals("javapath should be override by the additional arguments and as environment variable", expectedJavaPath, actualJavaPath);
    }

    @Test
    public void perform_overrideFilesStorageFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "filesstorage=fake-files-storage";
        additionalArguments += "\ntoken=" + validToken;

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner);

        //Assert
        String expectedFilesStorage = "fake-files-storage";
        String actualFilesStorage = cliHandler.getFilesStorage();

        Assert.assertEquals("filesstorage should be override by the additional arguments", expectedFilesStorage, actualFilesStorage);
    }

    @Test
    public void perform_overrideFilesStorageWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        String additionalArguments = "filesstorage=${FILES_STORAGE}";
        additionalArguments += "\ntoken=" + validToken;

        EnvVars envVars = new EnvVars();
        envVars.put("FILES_STORAGE", "fake-files-storage");

        CLIRunner cliRunner = createCLIRunner(additionalArguments);
        CLIHandler cliHandler = runPerformOverrideTest(cliRunner, envVars);

        //Assert
        String expectedFilesStorage = "fake-files-storage";
        String actualFilesStorage = cliHandler.getFilesStorage();

        Assert.assertEquals("filesstorage should be override by the additional arguments and as environment variable", expectedFilesStorage, actualFilesStorage);
    }

    private CLIHandler runPerformOverrideTest(CLIRunner cliRunner)
            throws IOException, InterruptedException {
        return runPerformOverrideTest(cliRunner, new EnvVars());
    }

    private CLIHandler runPerformOverrideTest(CLIRunner cliRunner, EnvVars envVars)
            throws IOException, InterruptedException {
        //Arrange
        CLIHandler cliHandler = new CLIHandlerMock(nullLogger);
        AbstractBuild<?, ?> build = createJenkinsBuildObject(envVars);
        BuildListener listener = mock(BuildListener.class);
        CommandMode commandMode = new CommandMode.EndView();
        //Act

        cliRunner.perform(build, null, listener, commandMode, cliHandler, nullLogger);
        return cliHandler;
    }

    private String createFieldOverrideString(String field, String value, String envVar) {
        String fieldOverride = field + "=";
        if (StringUtils.isNullOrEmpty(envVar)) {
            fieldOverride += value;
        } else {
            fieldOverride += "${" + envVar + "}";
        }

        return fieldOverride;
    }

    private AbstractBuild<?, ?> createJenkinsBuildObject(EnvVars envVars)
            throws IOException, InterruptedException {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        when(build.getEnvironment(any(TaskListener.class))).thenReturn(envVars);
        return build;
    }

    private BeginAnalysis createBeginAnalysis() {
        BeginAnalysis beginAnalysis = mock(BeginAnalysis.class);
        when(beginAnalysis.getDescriptor()).thenReturn(new BeginAnalysis.DescriptorImpl(true));

        return beginAnalysis;
    }

    private CLIRunner createCLIRunner(String additionalArgs) {
        CLIRunner cliRunner = new CLIRunner("", "", null, new CommandBuildName.EmptyBuildName(), "", additionalArgs);
        cliRunner.setBeginAnalysis(createBeginAnalysis());
        return cliRunner;
    }

    private class CLIHandlerMock extends CLIHandler {

        CLIHandlerMock(Logger logger) {
            super(logger);
        }

        @Override
        public boolean handle() {
            return true;
        }
    }

}
