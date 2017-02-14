package io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities;

/**
 * modes that can be executed.
 */
public enum CommandModes {
    Start("start") {
        @Override public String getDisplayName() {
            return "Step 1 - Start Test Execution";
        }
    },
    End("end") {
        @Override public String getDisplayName() {
            return "Step 3 - End Test Execution";
        }
    },
    UploadReports("uploadReports") {
        @Override public String getDisplayName() {
            return "Step 2 - Upload Report";
        }
    },
    ExternalReport("externalReport") {
        @Override public String getDisplayName() {
            return "Send external report";
        }
    },
    Config("config") {
        @Override public String getDisplayName() {
            return "Config - Create build session data";
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
