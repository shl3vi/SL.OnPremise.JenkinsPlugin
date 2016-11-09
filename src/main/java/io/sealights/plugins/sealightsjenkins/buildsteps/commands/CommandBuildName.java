package io.sealights.plugins.sealightsjenkins.buildsteps.commands;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.CommandBuildNamingStrategy;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

import java.io.Serializable;

/**
 * This class holds the different possible build names and their arguments in the UI
 */
public class CommandBuildName implements Describable<CommandBuildName>, ExtensionPoint, Serializable {

    private final CommandBuildNamingStrategy buildNamingStrategy;

    private CommandBuildName(final CommandBuildNamingStrategy buildNamingStrategy) {
        this.buildNamingStrategy = buildNamingStrategy;
    }

    @Exported
    public CommandBuildNamingStrategy getBuildNamingStrategy() {
        return buildNamingStrategy;
    }

    @Override
    public Descriptor<CommandBuildName> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static class CommandBuildNameDescriptor extends Descriptor<CommandBuildName> {

        private String buildNameSelection;

        protected CommandBuildNameDescriptor(final Class<? extends CommandBuildName> clazz, final String buildNameSelection) {
            super(clazz);
            this.buildNameSelection = buildNameSelection;
        }

        public boolean isDefault(){
            return false;
        }

        @Override
        public String getDisplayName() {
            return buildNameSelection;
        }

        public DescriptorExtensionList<CommandBuildName, CommandBuildNameDescriptor> getRepositoryLocationDescriptors() {
            return Hudson.getInstance().<CommandBuildName, CommandBuildNameDescriptor>getDescriptorList(CommandBuildName.class);
        }
    }

    public static class DefaultBuildName extends CommandBuildName {

        @DataBoundConstructor
        public DefaultBuildName() {
            super(CommandBuildNamingStrategy.JENKINS_BUILD);
        }

        @Extension
        public static class DefaultBuildNameDescriptor extends CommandBuildNameDescriptor {

            public DefaultBuildNameDescriptor() {
                super(DefaultBuildName.class, CommandBuildNamingStrategy.JENKINS_BUILD.getDisplayName());
            }

            @Override
            public boolean isDefault(){
                return true;
            }
        }

    }

    public static class ManualBuildName extends CommandBuildName {

        private String insertedBuildName;

        @DataBoundConstructor
        public ManualBuildName(String insertedBuildName) {
            super(CommandBuildNamingStrategy.MANUAL);
            this.insertedBuildName = insertedBuildName;
        }

        public String getInsertedBuildName() {
            return insertedBuildName;
        }

        public void setInsertedBuildName(String insertedBuildName) {
            this.insertedBuildName = insertedBuildName;
        }

        @Extension
        public static class ManualBuildNameDescriptor extends CommandBuildNameDescriptor {

            public ManualBuildNameDescriptor() {
                super(ManualBuildName.class, CommandBuildNamingStrategy.MANUAL.getDisplayName());
            }
        }

    }

    public static class UpstreamBuildName extends CommandBuildName {

        private String upstreamProjectName;

        @DataBoundConstructor
        public UpstreamBuildName(String upstreamProjectName) {
            super(CommandBuildNamingStrategy.JENKINS_UPSTREAM);
            this.upstreamProjectName = upstreamProjectName;
        }

        public String getUpstreamProjectName() {
            return upstreamProjectName;
        }

        public void setUpstreamProjectName(String upstreamProjectName) {
            this.upstreamProjectName = upstreamProjectName;
        }

        @Extension
        public static class UpstreamBuildNameDescriptor extends CommandBuildNameDescriptor {

            public UpstreamBuildNameDescriptor() {
                super(UpstreamBuildName.class, CommandBuildNamingStrategy.JENKINS_UPSTREAM.getDisplayName());
            }

            public FormValidation doCheckUpstreamProjectName(@QueryParameter String upstreamProjectName) {
                if (StringUtils.isNullOrEmpty(upstreamProjectName))
                    return FormValidation.error("Project Name is mandatory.");
                return FormValidation.ok();
            }
        }
    }

    public static class LatestBuildName extends CommandBuildName {

        @DataBoundConstructor
        public LatestBuildName() {
            super(CommandBuildNamingStrategy.LATEST_BUILD);
        }

        @Extension
        public static class LatestBuildNameDescriptor extends CommandBuildNameDescriptor {

            public LatestBuildNameDescriptor() {
                super(LatestBuildName.class, CommandBuildNamingStrategy.LATEST_BUILD.getDisplayName());
            }
        }
    }
}

