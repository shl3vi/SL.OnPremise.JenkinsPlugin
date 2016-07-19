package io.sealights.plugins.sealightsjenkins;

/**
 * Created by shahar on 4/28/2016.
 */
public enum BuildStrategy {
    ONE_BUILD() {
        @Override public String getDisplayName() {
            return "One Build";
        }
    },
    BUILD_EACH_MODULE() {
        @Override public String getDisplayName() {
            return "Build Per Module";
        }
    };
    public abstract String getDisplayName();
}
