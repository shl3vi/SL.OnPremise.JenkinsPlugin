package io.sealights.plugins.sealightsjenkins.integration.upgrade.entities;

public class Version implements Comparable<Version> {

    public static final int THIS_VERSION_NEWER = 1;
    public static final int THIS_VERSION_OLDER = -1;
    public static final int THIS_VERSION_EQUALS = 0;

    private String version;

    public final String get() {
        return this.version;
    }

    public final String toString(){
        return this.version;
    }

    public Version(String version) {
        if (version == null) {
            this.version = "0";
            return;
        }

        if (version.endsWith("-SNAPSHOT")) {
            version = version.replace("-SNAPSHOT", "");
        }

        if (!isValidVersion(version)) {
            this.version = "0";
        } else
            this.version = version;

    }

    public static boolean isValidVersion(String v){
        return v !=null && v.matches("[0-9]+(\\.[0-9]+)*");
    }

    public int compareTo(Version other) {
        if (other == null)
            return THIS_VERSION_NEWER;
        String[] thisParts = this.get().split("\\.");
        String[] otherParts = other.get().split("\\.");
        int length = Math.max(thisParts.length, otherParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int otherPart = i < otherParts.length ?
                    Integer.parseInt(otherParts[i]) : 0;
            if (thisPart < otherPart)
                return THIS_VERSION_OLDER;
            if (thisPart > otherPart)
                return THIS_VERSION_NEWER;
        }
        return THIS_VERSION_EQUALS;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        return this.compareTo((Version) other) == THIS_VERSION_EQUALS;
    }

}