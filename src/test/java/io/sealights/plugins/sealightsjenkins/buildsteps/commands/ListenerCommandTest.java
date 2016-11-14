package io.sealights.plugins.sealightsjenkins.buildsteps.commands;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import io.sealights.plugins.sealightsjenkins.BeginAnalysis;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListenerCommandTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void perform_overrideCustomerIdFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("customerid", "fake-customer");

        //Assert
        String expectedCustomerId = "fake-customer";
        String actualCustomerId = listenerCommandHandler.getBaseArgs().getCustomerId();

        Assert.assertEquals("customerid should be override by the additional arguments", expectedCustomerId, actualCustomerId);
    }

    @Test
    public void perform_overrideCustomerIdWithEnvVar_shouldResolveAndOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("customerid", "fake-customer", "TEST_CUSTOMER");

        //Assert
        String expectedCustomerId = "fake-customer";
        String actualCustomerId = listenerCommandHandler.getBaseArgs().getCustomerId();

        Assert.assertEquals("customerid should be override by the additional arguments", expectedCustomerId, actualCustomerId);
    }

    @Test
    public void perform_overrideServerFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("server", "fake-server");

        //Assert
        String expectedServer = "fake-server";
        String actualServer = listenerCommandHandler.getBaseArgs().getUrl();

        Assert.assertEquals("server should be override by the additional arguments", expectedServer, actualServer);
    }

    @Test
    public void perform_overrideServerWithEnvVar_shouldResolveAndOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("server", "fake-server", "SERVER");

        //Assert
        String expectedServer = "fake-server";
        String actualServer = listenerCommandHandler.getBaseArgs().getUrl();

        Assert.assertEquals("server should be override by the additional arguments", expectedServer, actualServer);
    }

    @Test
    public void perform_overrideProxyFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("proxy", "fake-proxy");

        //Assert
        String expectedProxy = "fake-proxy";
        String actualProxy = listenerCommandHandler.getBaseArgs().getProxy();

        Assert.assertEquals("proxy should be override by the additional arguments", expectedProxy, actualProxy);
    }

    @Test
    public void perform_overrideProxyWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("proxy", "fake-proxy", "PROXY");

        //Assert
        String expectedProxy = "fake-proxy";
        String actualProxy = listenerCommandHandler.getBaseArgs().getProxy();

        Assert.assertEquals("proxy should be override by the additional arguments", expectedProxy, actualProxy);
    }

    @Test
    public void perform_overrideAgentPathFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("agentpath", "fake-agent-path");

        //Assert
        String expectedAgentPath = "fake-agent-path";
        String actualAgentPath = listenerCommandHandler.getBaseArgs().getAgentPath();

        Assert.assertEquals("agentpath should be override by the additional arguments", expectedAgentPath, actualAgentPath);
    }

    @Test
    public void perform_overrideAgentPathWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("agentpath", "fake-agent-path", "AGENT_PATH");

        //Assert
        String expectedAgentPath = "fake-agent-path";
        String actualAgentPath = listenerCommandHandler.getBaseArgs().getAgentPath();

        Assert.assertEquals("agentpath should be override by the additional arguments", expectedAgentPath, actualAgentPath);
    }

    @Test
    public void perform_overrideJavaPathFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("javapath", "fake-java-path");

        //Assert
        String expectedJavaPath = "fake-java-path";
        String actualJavaPath = listenerCommandHandler.getBaseArgs().getJavaPath();

        Assert.assertEquals("javapath should be override by the additional arguments", expectedJavaPath, actualJavaPath);
    }

    @Test
    public void perform_overrideJavaPathWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("javapath", "fake-java-path", "JAVA_PATH");

        //Assert
        String expectedJavaPath = "fake-java-path";
        String actualJavaPath = listenerCommandHandler.getBaseArgs().getJavaPath();

        Assert.assertEquals("javapath should be override by the additional arguments", expectedJavaPath, actualJavaPath);
    }

    @Test
    public void perform_overrideFilesStorageFromAdditionalArgs_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("filesstorage", "fake-files-storage");

        //Assert
        String expectedFilesStorage = "fake-files-storage";
        String actualFilesStorage = listenerCommandHandler.getFilesStorage();

        Assert.assertEquals("filesstorage should be override by the additional arguments", expectedFilesStorage, actualFilesStorage);
    }

    @Test
    public void perform_overrideFilesStorageWithEnvVars_shouldOverride() throws IOException, InterruptedException {
        //Arrange & Act
        ListenerCommandHandler listenerCommandHandler = runPerformOverrideTest("filesstorage", "fake-files-storage", "FILES_STORAGE");

        //Assert
        String expectedFilesStorage = "fake-files-storage";
        String actualFilesStorage = listenerCommandHandler.getFilesStorage();

        Assert.assertEquals("filesstorage should be override by the additional arguments", expectedFilesStorage, actualFilesStorage);
    }


    private ListenerCommandHandler runPerformOverrideTest(String field, String value)
            throws IOException, InterruptedException {
        return runPerformOverrideTest(field, value, null);
    }

    private ListenerCommandHandler runPerformOverrideTest(String field, String value, String envVar)
            throws IOException, InterruptedException {
        //Arrange
        String fieldOverride = createFieldOverrideString(field, value, envVar);
        ListenerCommand listenerCommand = createListenerCommand(fieldOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);
        AbstractBuild<?, ?> build = createJenkinsBuildObject(envVar, value);

        //Act
        performListenerCommand(listenerCommand, listenerCommandHandler, build);

        return listenerCommandHandler;
    }

    private String createFieldOverrideString(String field, String value, String envVar){
        String fieldOverride = field + "=";
        if (StringUtils.isNullOrEmpty(envVar)) {
            fieldOverride += value;
        } else {
            fieldOverride += "${"+envVar+"}";
        }

        return fieldOverride;
    }
    private AbstractBuild<?, ?> createJenkinsBuildObject(String envKey, String envValue)
            throws IOException, InterruptedException {

        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        EnvVars envVars = new EnvVars();
        if (!StringUtils.isNullOrEmpty(envKey)) {
            envVars.put(envKey, envValue);
        }
        when(build.getEnvironment(any(TaskListener.class))).thenReturn(envVars);

        return build;
    }

    private void performListenerCommand(ListenerCommand listenerCommand, ListenerCommandHandler listenerCommandHandler,
                                        AbstractBuild<?, ?> build)
            throws IOException, InterruptedException {
        BuildListener listener = mock(BuildListener.class);
        listenerCommand.setBeginAnalysis(createBeginAnalysis());
        CommandMode commandMode = new CommandMode.EndView();
        listenerCommand.perform(build, null, listener, commandMode, listenerCommandHandler, nullLogger);
    }

    private BeginAnalysis createBeginAnalysis() {
        BeginAnalysis beginAnalysis = mock(BeginAnalysis.class);
        when(beginAnalysis.getDescriptor()).thenReturn(new BeginAnalysis.DescriptorImpl(true));

        return beginAnalysis;
    }

    private ListenerCommand createListenerCommand(String additionalArgs) {
        return new ListenerCommand("", "", null, "", additionalArgs);
    }

    private class ListenerCommandHandlerMock extends ListenerCommandHandler {

        ListenerCommandHandlerMock(Logger logger) {
            super(logger);
        }

        @Override
        public boolean handle() {
            return true;
        }
    }

}
