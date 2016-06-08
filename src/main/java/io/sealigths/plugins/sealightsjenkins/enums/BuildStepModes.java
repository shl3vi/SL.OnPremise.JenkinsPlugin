package io.sealigths.plugins.sealightsjenkins.enums;

/**
 * Created by Nadav on 6/7/2016.
 */
public enum BuildStepModes {
    Off() {
        @Override public String getDisplayName() {
            return "Invoke top-level Maven targets (Sealights is disabled)";
        }
    },
    InvokeMavenCommand() {
        @Override public String getDisplayName() {
            return "Invoke top-level Maven targets with Sealights Continuous Testing";
        }
    },
    PrepareSealights() {
        @Override public String getDisplayName() {
            return "Prepare Sealights";
        }
    };

    public abstract String getDisplayName();
}
