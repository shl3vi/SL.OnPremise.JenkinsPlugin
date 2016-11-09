package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * The optionals strategies to get build name.
 */
public enum CommandBuildNamingStrategy {
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
    },
    LATEST_BUILD() {
        @Override public String getDisplayName() {
            return "Report on latest build.";
        }
    };
    public abstract String getDisplayName();
}

