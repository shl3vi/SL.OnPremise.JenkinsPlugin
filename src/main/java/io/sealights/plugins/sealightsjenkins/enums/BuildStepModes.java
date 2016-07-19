package io.sealights.plugins.sealightsjenkins.enums;

/**
 * Created by Nadav on 6/7/2016.
 */
public enum BuildStepModes {
    Off() {
        @Override public String getDisplayName() {
            return "Disable this build step - FOR DEBUGGING PURPOSES ONLY";
        }
    },
    InvokeMavenCommand() {
        @Override public String getDisplayName() {
            return "Invoke top-level Maven targets (Sealights is disabled) - FOR DEBUGGING PURPOSES ONLY";
        }
    },
    InvokeMavenCommandWithSealights() {
        @Override public String getDisplayName() {
            return "Invoke top-level Maven targets with Sealights Continuous Testing";
        }
    },
    PrepareSealights() {
        @Override public String getDisplayName() {
            return "Integrate Sealights into POM files. Requires adding a 'SeaLights Continuous Testing - Cleanup' build step";
        }
    };

    public abstract String getDisplayName();
}
