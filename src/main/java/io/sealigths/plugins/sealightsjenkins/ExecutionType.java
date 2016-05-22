package io.sealigths.plugins.sealightsjenkins;

/**
 * Created by shahar on 4/25/2016.
 */
public enum ExecutionType {
    FULL() {
        @Override public String getDisplayName() {
            return "Build & Tests";
        }
    },
    ONLY_LISTENER() {
        @Override public String getDisplayName() {
            return "Only Tests";
        }
    };
    public abstract String getDisplayName();
}
