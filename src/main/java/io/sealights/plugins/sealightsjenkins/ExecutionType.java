package io.sealights.plugins.sealightsjenkins;

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
    },

    @Deprecated
    ONLY_LISTENER() {
        @Override public String getDisplayName() {
            return "Only Tests";
        }
    };
    public abstract String getDisplayName();
}
