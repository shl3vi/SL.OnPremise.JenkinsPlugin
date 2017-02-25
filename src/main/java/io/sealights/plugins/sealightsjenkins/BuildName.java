package io.sealights.plugins.sealightsjenkins;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Items;
import hudson.util.FormValidation;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

import java.io.Serializable;

/**
 * Created by shahar on 5/15/2016.
 */
public class BuildName implements Describable<BuildName>, ExtensionPoint, Serializable {

    private final BuildNamingStrategy buildNamingStrategy;

    private BuildName(final BuildNamingStrategy buildNamingStrategy) {
        this.buildNamingStrategy = buildNamingStrategy;
    }

    @Exported
    public BuildNamingStrategy getBuildNamingStrategy() {
        return buildNamingStrategy;
    }

    @Override
    public Descriptor<BuildName> getDescriptor() {
        return Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    public static class BuildNameDescriptor extends Descriptor<BuildName> {

        private String buildNameSelection;

        protected BuildNameDescriptor(final Class<? extends BuildName> clazz, final String buildNameSelection) {
            super(clazz);
            this.buildNameSelection = buildNameSelection;
        }

        public boolean isDefault(){
            return false;
        }

        public boolean isEmptyBuild(){
            return false;
        }

        @Override
        public String getDisplayName() {
            return buildNameSelection;
        }

        public DescriptorExtensionList<BuildName, BuildNameDescriptor> getRepositoryLocationDescriptors() {
            return Hudson.getInstance().<BuildName, BuildNameDescriptor>getDescriptorList(BuildName.class);
        }
    }

    public static class DefaultBuildName extends BuildName {

        @DataBoundConstructor
        public DefaultBuildName() {
            super(BuildNamingStrategy.JENKINS_BUILD);
        }

        @Extension
        public static class DefaultBuildNameDescriptor extends BuildNameDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildName$DefaultBuildName", DefaultBuildName.class);
            }

            public DefaultBuildNameDescriptor() {
                super(DefaultBuildName.class, BuildNamingStrategy.JENKINS_BUILD.getDisplayName());
            }

            @Override
            public boolean isDefault(){
                return true;
            }
        }

    }

    public static class ManualBuildName extends BuildName {

        private String insertedBuildName;

        @DataBoundConstructor
        public ManualBuildName(String insertedBuildName) {
            super(BuildNamingStrategy.MANUAL);
            this.insertedBuildName = insertedBuildName;
        }

        public String getInsertedBuildName() {
            return insertedBuildName;
        }

        public void setInsertedBuildName(String insertedBuildName) {
            this.insertedBuildName = insertedBuildName;
        }

        @Extension
        public static class ManualBuildNameDescriptor extends BuildNameDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildName$ManualBuildName", ManualBuildName.class);
            }

            public ManualBuildNameDescriptor() {
                super(ManualBuildName.class, BuildNamingStrategy.MANUAL.getDisplayName());
            }
        }

    }

    public static class UpstreamBuildName extends BuildName {

        private String upstreamProjectName;

        @DataBoundConstructor
        public UpstreamBuildName(String upstreamProjectName) {
            super(BuildNamingStrategy.JENKINS_UPSTREAM);
            this.upstreamProjectName = upstreamProjectName;
        }

        public String getUpstreamProjectName() {
            return upstreamProjectName;
        }

        public void setUpstreamProjectName(String upstreamProjectName) {
            this.upstreamProjectName = upstreamProjectName;
        }

        @Extension
        public static class UpstreamBuildNameDescriptor extends BuildNameDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildName$UpstreamBuildName", UpstreamBuildName.class);
            }

            public UpstreamBuildNameDescriptor() {
                super(UpstreamBuildName.class, BuildNamingStrategy.JENKINS_UPSTREAM.getDisplayName());
            }

            public FormValidation doCheckUpstreamProjectName(@QueryParameter String upstreamProjectName) {
                if (StringUtils.isNullOrEmpty(upstreamProjectName))
                    return FormValidation.error("Project Name is mandatory.");
                return FormValidation.ok();
            }
        }
    }

    public static class LatestBuildName extends BuildName {

        @DataBoundConstructor
        public LatestBuildName() {
            super(BuildNamingStrategy.LATEST_BUILD);
        }

        @Extension
        public static class LatestBuildNameDescriptor extends BuildNameDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildName$LatestBuildName", LatestBuildName.class);
            }

            public LatestBuildNameDescriptor() {
                super(LatestBuildName.class, BuildNamingStrategy.LATEST_BUILD.getDisplayName());
            }
        }
    }

    public static class EmptyBuildName extends BuildName {

        @DataBoundConstructor
        public EmptyBuildName() {
            super(BuildNamingStrategy.EMPTY_BUILD);
        }

        @Extension
        public static class EmptyBuildNameDescriptor extends BuildNameDescriptor {

            public EmptyBuildNameDescriptor() {
                super(EmptyBuildName.class, BuildNamingStrategy.EMPTY_BUILD.getDisplayName());
            }

            public boolean isEmptyBuild(){
                return true;
            }
        }
    }
}
