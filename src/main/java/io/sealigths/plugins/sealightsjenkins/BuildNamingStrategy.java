package io.sealigths.plugins.sealightsjenkins;

/**
 * Created by Alon on 5/15/2016.
 */
public enum BuildNamingStrategy {
    JENKINS_BUILD() {
        @Override public String getDisplayName() {
            return "Use the job name from Jenkins (by default, an auto-incrementing number)";
        }
    },
    JENKINS_UPSTREAM() {
        @Override public String getDisplayName() {
            return "Use the name from an upstream Jenkins job.";
        }
    },
    MANUAL() {
        @Override public String getDisplayName() {
            return "Specify a custom build name. (Allows passing expressions)";
        }
    };
    public abstract String getDisplayName();
}
