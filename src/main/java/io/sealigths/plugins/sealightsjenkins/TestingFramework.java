package io.sealigths.plugins.sealightsjenkins;

/**
 * Created by shahar on 4/24/2016.
 */
public enum TestingFramework {
    TESTNG() {
        @Override public String getDisplayName() {
            return "testNG";
        }
    },
    JUNIT() {
        @Override public String getDisplayName() {
            return "JUnit";
        }
    },
    JUNIT_AND_TESTNG() {
        @Override public String getDisplayName() {
            return "JUnit_&_testNG";
        }
    };
    public abstract String getDisplayName();
}
