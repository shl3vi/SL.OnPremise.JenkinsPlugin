package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

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
    },
    EMPTY_BUILD() {
        @Override public String getDisplayName() {
            return "Leave build name field empty.";
        }
    };
    public abstract String getDisplayName();
}

