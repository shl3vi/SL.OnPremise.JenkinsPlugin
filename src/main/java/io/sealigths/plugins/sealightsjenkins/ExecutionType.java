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
    TESTS_ONLY() {
        @Override public String getDisplayName() {
            return "Tests Only";
        }
    };
    public abstract String getDisplayName();
}
