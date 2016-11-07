package io.sealights.plugins.sealightsjenkins.integration.upgrade.entities;

public enum ComponentName {
    TEST_LISTENER_COMPONENT_NAME("sl-test-listener"),
    BUILD_SCANNER_COMPONENT_NAME("sl-build-scanner");

    private final String name;

    ComponentName(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

}