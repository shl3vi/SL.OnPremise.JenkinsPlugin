package io.sealigths.plugins.sealightsjenkins;

/**
 * Created by shahar on 4/24/2016.
 */
public enum LogDestination {
    CONSOLE() {
        @Override public String getDisplayName() {
            return "Console";
        }
    },
    FILE() {
        @Override public String getDisplayName() {
            return "File";
        }
    };
    public abstract String getDisplayName();
}
