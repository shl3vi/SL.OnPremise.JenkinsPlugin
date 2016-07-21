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
import io.sealights.plugins.sealightsjenkins.enums.BuildStepModes;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import java.io.Serializable;

/**
 * Created by Nadav on 5/15/2016.
 */
public class BuildStepMode implements Describable<BuildStepMode>, ExtensionPoint, Serializable {

    private final BuildStepModes currentMode;

    private BuildStepMode(final BuildStepModes currentMode) {
        this.currentMode = currentMode;
    }

    @Exported
    public BuildStepModes getCurrentMode() {
        return currentMode;
    }

    @Override
    public Descriptor<BuildStepMode> getDescriptor() {
        return Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    public static class BuildStepModeDescriptor extends Descriptor<BuildStepMode> {

        private String selectedMode;

        protected BuildStepModeDescriptor(final Class<? extends BuildStepMode> clazz, final String selectedMode) {
            super(clazz);
            this.selectedMode = selectedMode;
        }

        public boolean isDefault(){
            return false;
        }

        @Override
        public String getDisplayName() {
            return selectedMode;
        }

        public DescriptorExtensionList<BuildStepMode, BuildStepModeDescriptor> getRepositoryLocationDescriptors() {
            return Hudson.getInstance().<BuildStepMode, BuildStepModeDescriptor>getDescriptorList(BuildStepMode.class);
        }
    }

    public static class OffView extends BuildStepMode {

        @DataBoundConstructor
        public OffView() {
            super(BuildStepModes.Off);
        }

        @Extension
        public static class OffDescriptor extends BuildStepModeDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildStepMode$OffView", OffView.class);
            }

            public OffDescriptor() {
                super(OffView.class, BuildStepModes.Off.getDisplayName());
            }
        }

    }

    public static class DisableSealightsView extends BuildStepMode {

        private String targets;

        @DataBoundConstructor
        public DisableSealightsView(String targets) {
            super(BuildStepModes.InvokeMavenCommand);
            this.targets = targets;
        }

        public String getTargets() {
            return targets;
        }

        public void setTargets(String targets) {
            this.targets = targets;
        }

        @Extension
        public static class DisableSealightsDescriptor extends BuildStepModeDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildStepMode$DisableSealightsView", DisableSealightsView.class);
            }

            public DisableSealightsDescriptor() {
                super(DisableSealightsView.class, BuildStepModes.InvokeMavenCommand.getDisplayName());
            }
        }

    }

    public static class InvokeMavenCommandView extends BuildStepMode {
        private String targets;

        @DataBoundConstructor
        public InvokeMavenCommandView(String targets) {
            super(BuildStepModes.InvokeMavenCommandWithSealights);
            this.targets = targets;
        }

        public String getTargets() {
            return targets;
        }

        public void setTargets(String targets) {
            this.targets = targets;
        }

        @Extension
        public static class InvokeMavenCommandDescriptor extends BuildStepModeDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildStepMode$InvokeMavenCommandView", InvokeMavenCommandView.class);
            }

            public InvokeMavenCommandDescriptor() {
                super(InvokeMavenCommandView.class, BuildStepModes.InvokeMavenCommandWithSealights.getDisplayName());
            }

            @Override
            public boolean isDefault(){
                return true;
            }
        }

    }


    public static class PrepareSealightsView extends BuildStepMode {

        private String additionalMavenArguments;

        @DataBoundConstructor
        public PrepareSealightsView(String additionalMavenArguments) {
            super(BuildStepModes.PrepareSealights);
            this.additionalMavenArguments = additionalMavenArguments;
        }

        public String getAdditionalMavenArguments() {
            return additionalMavenArguments;
        }

        public void setAdditionalMavenArguments(String additionalMavenArguments) {
            this.additionalMavenArguments = additionalMavenArguments;
        }

        @Extension
        public static class PrepareSealightsDescriptor extends BuildStepModeDescriptor {

            @Initializer(before = InitMilestone.PLUGINS_STARTED)
            public static void addAliases() {
                Items.XSTREAM2.addCompatibilityAlias("io.sealigths.plugins.sealightsjenkins.BuildStepMode$PrepareSealightsView", PrepareSealightsView.class);
            }

            public PrepareSealightsDescriptor() {
                super(PrepareSealightsView.class, BuildStepModes.PrepareSealights.getDisplayName());
            }
        }
    }

}
