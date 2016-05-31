package io.sealigths.plugins.sealightsjenkins;

/**
 * Created by shahar on 5/30/2016.
 */
public enum BuildInformationStrategy {

    JENKINS_UPSTREAM() {
        @Override public String getDisplayName() {
            return "Use information from an upstream Jenkins job.";
        }
    },
    MANUAL() {
        @Override public String getDisplayName() {
            return "Specify custom information. (Allows passing expressions)";
        }
    };
    public abstract String getDisplayName();
}