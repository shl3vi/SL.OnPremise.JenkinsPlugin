package io.sealigths.plugins.sealightsjenkins;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import io.sealigths.plugins.sealightsjenkins.utils.StringUtils;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

import java.io.Serializable;

/**
 * Created by shahar on 5/15/2016.
 */
public class BuildInformation implements Describable<BuildInformation>, ExtensionPoint, Serializable {

    private final BuildInformationStrategy buildInformationStrategy;

    private BuildInformation(final BuildInformationStrategy buildInformationStrategy) {
        this.buildInformationStrategy = buildInformationStrategy;
    }

    @Exported
    public BuildInformationStrategy getBuildInformationStrategy() {
        return buildInformationStrategy;
    }

    @Override
    public Descriptor<BuildInformation> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static class BuildInformationDescriptor extends Descriptor<BuildInformation> {

        private String buildInformationSelection;

        protected BuildInformationDescriptor(final Class<? extends BuildInformation> clazz, final String buildInformationSelection) {
            super(clazz);
            this.buildInformationSelection = buildInformationSelection;
        }

        @Override
        public String getDisplayName() {
            return buildInformationSelection;
        }

        public DescriptorExtensionList<BuildInformation, BuildInformationDescriptor> getRepositoryLocationDescriptors() {
            return Hudson.getInstance().<BuildInformation, BuildInformationDescriptor>getDescriptorList(BuildInformation.class);
        }
    }

    public static class ManualBuildInformation extends BuildInformation {

        private String insertedBuildName;
        private String insertedBranchName;
        private String insertedAppName;

        @DataBoundConstructor
        public ManualBuildInformation(String insertedAppName, String insertedBranchName, String insertedBuildName) {
            super(BuildInformationStrategy.MANUAL);

            this.insertedAppName = insertedAppName;
            this.insertedBranchName = insertedBranchName;
            this.insertedBuildName = insertedBuildName;
        }

        public String getInsertedBuildName() {
            return insertedBuildName;
        }

        public void setInsertedBuildName(String insertedBuildName) {
            this.insertedBuildName = insertedBuildName;
        }

        public String getInsertedBranchName() {
            return insertedBranchName;
        }

        public void setInsertedBranchName(String insertedBranchName) {
            this.insertedBranchName = insertedBranchName;
        }

        public String getInsertedAppName() {
            return insertedAppName;
        }

        public void setInsertedAppName(String insertedAppName) {
            this.insertedAppName = insertedAppName;
        }

        @Extension
        public static class ManualBuildInformationDescriptor extends BuildInformationDescriptor {
            public ManualBuildInformationDescriptor() {
                super(ManualBuildInformation.class, BuildNamingStrategy.MANUAL.getDisplayName());
            }
        }

    }

    public static class UpstreamBuildInformation extends BuildInformation {

        private String upstreamProjectName;

        @DataBoundConstructor
        public UpstreamBuildInformation(String upstreamProjectName) {
            super(BuildInformationStrategy.JENKINS_UPSTREAM);
            this.upstreamProjectName = upstreamProjectName;
        }

        public String getUpstreamProjectName() {
            return upstreamProjectName;
        }

        public void setUpstreamProjectName(String upstreamProjectName) {
            this.upstreamProjectName = upstreamProjectName;
        }

        @Extension
        public static class UpstreamBuildInformationDescriptor extends BuildInformationDescriptor {
            public UpstreamBuildInformationDescriptor() {
                super(UpstreamBuildInformation.class, BuildNamingStrategy.JENKINS_UPSTREAM.getDisplayName());
            }

            public FormValidation doCheckUpstreamProjectName(@QueryParameter String upstreamProjectName) {
                if (StringUtils.isNullOrEmpty(upstreamProjectName))
                    return FormValidation.error("Project Name is mandatory.");
                return FormValidation.ok();
            }
        }

    }

}
