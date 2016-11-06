package io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities;

/**
 * Created by shahar on 11/3/2016.
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
            return "Upload JUnit tests reports";
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
