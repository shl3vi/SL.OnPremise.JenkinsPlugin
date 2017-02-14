package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.remoting.Callable;
import hudson.remoting.LocalChannel;
import hudson.remoting.VirtualChannel;
import hudson.slaves.NodeDescriptor;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.ClockDifference;
import hudson.util.DescribableList;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.mockito.Mockito.mock;

/**
 * Created by shahar on 2/14/2017.
 */
public class TestHelper {

    public BuildMock createBuildMock() throws IOException {
        return new BuildMock(mock(FreeStyleProject.class));
    }

    class BuildMock extends FreeStyleBuild {

        public BuildMock(FreeStyleProject project) throws IOException {
            super(project);
            VirtualChannel vc = new LocalChannel(null);
            FilePath fp = new FilePath(vc, "/path/to/workspace");
            setWorkspace(fp);
        }

        @Override
        public Node getBuiltOn() {
            return new NodeMock();
        }
    }

    class NodeMock extends Node{

        @Nonnull
        @Override
        public String getNodeName() {
            return null;
        }

        @Override
        public void setNodeName(String s) {

        }

        @Override
        public String getNodeDescription() {
            return null;
        }

        @Override
        public Launcher createLauncher(TaskListener taskListener) {
            return null;
        }

        @Override
        public int getNumExecutors() {
            return 0;
        }

        @Override
        public Mode getMode() {
            return null;
        }

        @Override
        protected Computer createComputer() {
            return null;
        }

        @Override
        public String getLabelString() {
            return null;
        }

        @Override
        public FilePath getWorkspaceFor(TopLevelItem topLevelItem) {
            return null;
        }

        @Override
        public FilePath getRootPath() {
            return null;
        }

        @Nonnull
        @Override
        public DescribableList<NodeProperty<?>, NodePropertyDescriptor> getNodeProperties() {
            return null;
        }

        @Override
        public NodeDescriptor getDescriptor() {
            return null;
        }

        @Override
        public Callable<ClockDifference, IOException> getClockDifferenceCallable() {
            return null;
        }

        @Override
        public FilePath createPath(String absolutePath) {
            VirtualChannel vc = new LocalChannel(null);
            return new FilePath(vc, "/path/to/workspace");
        }
    }
}
