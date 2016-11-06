package io.sealights.plugins.sealightsjenkins.buildsteps.commands;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandModes;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import java.io.Serializable;

/**
 * Created by shahar on 11/3/2016.
 */
public class CommandMode implements Describable<CommandMode>, ExtensionPoint, Serializable {

    private final CommandModes currentMode;

    private CommandMode(final CommandModes currentMode) {
        this.currentMode = currentMode;
    }

    @Exported
    public CommandModes getCurrentMode() {
        return currentMode;
    }

    @Override
    public Descriptor<CommandMode> getDescriptor() {
        return Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    public static class CommandModeDescriptor extends Descriptor<CommandMode> {

        private String selectedMode;

        protected CommandModeDescriptor(final Class<? extends CommandMode> clazz, final String selectedMode) {
            super(clazz);
            this.selectedMode = selectedMode;
        }

        public boolean isDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return selectedMode;
        }

        public DescriptorExtensionList<CommandMode, CommandModeDescriptor> getRepositoryLocationDescriptors() {
            return Hudson.getInstance().getDescriptorList(CommandMode.class);
        }
    }

    public static class StartView extends CommandMode {

        private String newEnvironment;

        @DataBoundConstructor
        public StartView(String newEnvironment) {
            super(CommandModes.Start);
            this.newEnvironment = newEnvironment;
        }

        public String getNewEnvironment() {
            return newEnvironment;
        }

        public void setNewEnvironment(String newEnvironment) {
            this.newEnvironment = newEnvironment;
        }

        @Extension
        public static class StartDescriptor extends CommandModeDescriptor {

            @Override
            public boolean isDefault() {
                return true;
            }

            public StartDescriptor() {
                super(StartView.class, CommandModes.Start.getDisplayName());
            }
        }

    }

    public static class EndView extends CommandMode {

        @DataBoundConstructor
        public EndView() {
            super(CommandModes.End);
        }

        @Extension
        public static class EndDescriptor extends CommandModeDescriptor {
            public EndDescriptor() {
                super(EndView.class, CommandModes.End.getDisplayName());
            }
        }

    }

    public static class UploadReportsView extends CommandMode {

        private String reportFiles;
        private String reportsFolders;
        private boolean hasMoreRequests;
        private String source;

        @DataBoundConstructor
        public UploadReportsView(String reportFiles, String reportsFolders, boolean hasMoreRequests, String source) {
            super(CommandModes.UploadReports);
            this.reportFiles = reportFiles;
            this.reportsFolders = reportsFolders;
            this.hasMoreRequests = hasMoreRequests;
            this.source = source;
        }

        public String getReportFiles() {
            return reportFiles;
        }

        public void setReportFiles(String reportFiles) {
            this.reportFiles = reportFiles;
        }

        public String getReportsFolders() {
            return reportsFolders;
        }

        public void setReportsFolders(String reportsFolders) {
            this.reportsFolders = reportsFolders;
        }

        public boolean getHasMoreRequests() {
            return hasMoreRequests;
        }

        public void setHasMoreRequests(boolean hasMoreRequests) {
            this.hasMoreRequests = hasMoreRequests;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Extension
        public static class UploadReportsDescriptor extends CommandModeDescriptor {
            public UploadReportsDescriptor() {
                super(UploadReportsView.class, CommandModes.UploadReports.getDisplayName());
            }
        }

    }
}
