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
    JUNIT_4() {
        @Override public String getDisplayName() {
            return "JUnit 4";
        }
    },
    JUNIT_3() {
        @Override public String getDisplayName() {
            return "JUnit 3";
        }
    },
    JUNIT4_AND_TESTNG() {
        @Override public String getDisplayName() {
            return "JUnit 4 & testNG";
        }
    },
    AUTO_DETECT() {
        @Override public String getDisplayName() {
            return "Auto Detect";
        }
    };
    public abstract String getDisplayName();
}
