package io.sealights.plugins.sealightsjenkins.buildsteps.commands;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import io.sealights.plugins.sealightsjenkins.BeginAnalysis;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListenerCommandTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void overrideCustomerId() throws IOException, InterruptedException {

        String customerIdOverride = "customerid=fake-customer";
        ListenerCommand listenerCommand = createListenerCommand(customerIdOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);

        performListenerCommand(listenerCommand, listenerCommandHandler);

        String expectedCustomerId = "fake-customer";
        String actualCustomerId = listenerCommandHandler.getBaseArgs().getCustomerId();

        Assert.assertEquals("customerid should be override by the additional arguments", expectedCustomerId, actualCustomerId);
    }

    @Test
    public void overrideServer() throws IOException, InterruptedException {

        String serverOverride = "server=fake-server";
        ListenerCommand listenerCommand = createListenerCommand(serverOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);

        performListenerCommand(listenerCommand, listenerCommandHandler);

        String expectedServer = "fake-server";
        String actualServer = listenerCommandHandler.getBaseArgs().getUrl();

        Assert.assertEquals("server should be override by the additional arguments", expectedServer, actualServer);
    }

    @Test
    public void overrideProxy() throws IOException, InterruptedException {

        String proxyOverride = "proxy=fake-proxy";
        ListenerCommand listenerCommand = createListenerCommand(proxyOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);

        performListenerCommand(listenerCommand, listenerCommandHandler);

        String expectedProxy = "fake-proxy";
        String actualProxy = listenerCommandHandler.getBaseArgs().getProxy();

        Assert.assertEquals("proxy should be override by the additional arguments", expectedProxy, actualProxy);
    }

    @Test
    public void overrideAgentPath() throws IOException, InterruptedException {

        String agentPathOverride = "agentpath=fake-agent-path";
        ListenerCommand listenerCommand = createListenerCommand(agentPathOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);

        performListenerCommand(listenerCommand, listenerCommandHandler);

        String expectedAgentPath = "fake-agent-path";
        String actualAgentPath = listenerCommandHandler.getBaseArgs().getAgentPath();

        Assert.assertEquals("agentpath should be override by the additional arguments", expectedAgentPath, actualAgentPath);
    }

    @Test
    public void overrideJavaPath() throws IOException, InterruptedException {

        String javaPathOverride = "javapath=fake-java-path";
        ListenerCommand listenerCommand = createListenerCommand(javaPathOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);

        performListenerCommand(listenerCommand, listenerCommandHandler);

        String expectedJavaPath = "fake-java-path";
        String actualJavaPath = listenerCommandHandler.getBaseArgs().getJavaPath();

        Assert.assertEquals("javapath should be override by the additional arguments", expectedJavaPath, actualJavaPath);
    }

    @Test
    public void overrideFileStorage() throws IOException, InterruptedException {

        String filesStorageOverride = "filesstorage=fake-files-storage";
        ListenerCommand listenerCommand = createListenerCommand(filesStorageOverride);
        ListenerCommandHandler listenerCommandHandler = new ListenerCommandHandlerMock(nullLogger);

        performListenerCommand(listenerCommand, listenerCommandHandler);

        String expectedJavaPath = "fake-files-storage";
        String actualJavaPath = listenerCommandHandler.getFilesStorage();

        Assert.assertEquals("filesstorage should be override by the additional arguments", expectedJavaPath, actualJavaPath);
    }

    private void performListenerCommand(ListenerCommand listenerCommand, ListenerCommandHandler listenerCommandHandler) throws IOException, InterruptedException {
        BuildListener listener = mock(BuildListener.class);
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        when(build.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars());
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
        return new ListenerCommand("","",null,"",additionalArgs);
    }

    private class ListenerCommandHandlerMock extends ListenerCommandHandler {

        ListenerCommandHandlerMock(Logger logger) {
            super(logger);
        }

        @Override
        public boolean handle(){
            return true;
        }
    }

}
