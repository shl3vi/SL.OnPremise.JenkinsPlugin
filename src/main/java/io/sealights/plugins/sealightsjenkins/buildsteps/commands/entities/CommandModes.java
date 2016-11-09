package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * modes that can be executed.
 */
public enum CommandModes {
    Start("start") {
        @Override public String getDisplayName() {
            return "Start new execution";
        }
    },
    End("end") {
        @Override public String getDisplayName() {
            return "End execution";
        }
    },
    UploadReports("uploadReports") {
        @Override public String getDisplayName() {
            return "Upload tests reports";
        }
    };

    private final String name;

    CommandModes(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract String getDisplayName();
}
