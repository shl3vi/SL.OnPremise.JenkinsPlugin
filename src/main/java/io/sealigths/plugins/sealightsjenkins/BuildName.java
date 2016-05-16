package io.sealigths.plugins.sealightsjenkins;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
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
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static class BuildNameDescriptor extends Descriptor<BuildName> {

        private String buildNameSelection;

        protected BuildNameDescriptor(final Class<? extends BuildName> clazz, final String buildNameSelection) {
            super(clazz);
            this.buildNameSelection = buildNameSelection;
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
            public DefaultBuildNameDescriptor() {
                super(DefaultBuildName.class, BuildNamingStrategy.JENKINS_BUILD.getDisplayName());
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
            public ManualBuildNameDescriptor() {
                super(ManualBuildName.class, BuildNamingStrategy.MANUAL.getDisplayName());
            }
        }

    }

//    public static class UpstreamBuildName extends BuildName {
//
//        @DataBoundConstructor
//        public UpstreamBuildName() {
//            super(BuildNamingStrategy.JENKINS_UPSTREAM);
//        }
//
//        @Extension
//        public static class UpstreamBuildNameDescriptor extends BuildNameDescriptor {
//            public UpstreamBuildNameDescriptor() {
//                super(UpstreamBuildName.class, BuildNamingStrategy.JENKINS_UPSTREAM.getDisplayName());
//            }
//        }
//
//    }

}
