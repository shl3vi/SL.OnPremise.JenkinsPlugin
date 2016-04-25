package io.sealigths.plugins.sealightsjenkins;

/**
 * Created by shahar on 4/25/2016.
 */
public enum ProjectType {
    MAVEN() {
        @Override public String getDisplayName() {
            return "maven";
        }
    },
    GRADLE() {
        @Override public String getDisplayName() {
            return "gradle";
        }
    };
    public abstract String getDisplayName();
}
