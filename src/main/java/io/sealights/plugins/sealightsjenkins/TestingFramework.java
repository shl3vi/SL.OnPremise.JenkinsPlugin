package io.sealights.plugins.sealightsjenkins;

@Deprecated
public enum TestingFramework {
    TESTNG() {
        @Override public String getDisplayName() {
            return "testNG";
        }
    },
    @Deprecated
    JUNIT() {
        @Override public String getDisplayName() {
            return "JUnit";
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
