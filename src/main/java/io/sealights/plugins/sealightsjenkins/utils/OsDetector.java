package io.sealights.plugins.sealightsjenkins.utils;

/**
 * Created by shahar on 11/8/2016.
 */
public class OsDetector {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
    public static String getOS(){
        if (isWindows()) {
            return "Windows";
        } else if (isMac()) {
            return "Mac";
        } else if (isUnix()) {
            return "Unix";
        } else if (isSolaris()) {
            return "Solaris";
        } else {
            return "unknown";
        }
    }

}