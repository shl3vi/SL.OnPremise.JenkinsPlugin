package io.sealigths.plugins.sealightsjenkins;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import io.sealigths.plugins.sealightsjenkins.enums.BuildStepModes;
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
            public InvokeMavenCommandDescriptor() {
                super(InvokeMavenCommandView.class, BuildStepModes.InvokeMavenCommandWithSealights.getDisplayName());
            }
        }

    }

    public static class PrepareSealightsView extends BuildStepMode {
        @DataBoundConstructor
        public PrepareSealightsView() {
            super(BuildStepModes.PrepareSealights);
        }

        @Extension
        public static class PrepareSealightsDescriptor extends BuildStepModeDescriptor {
            public PrepareSealightsDescriptor() {
                super(PrepareSealightsView.class, BuildStepModes.PrepareSealights.getDisplayName());
            }
        }
    }

}
