package io.sealights.plugins.sealightsjenkins.utils;

/**
 * Created by shahar on 2/7/2017.
 */
public class ProcessUtils {

    public int waitFor(Process process, int timeoutInSeconds) throws InterruptedException {
        if (timeoutInSeconds <= 0) {
            return process.waitFor();
        } else {
            long now = System.currentTimeMillis();
            long timeoutInMillis = 1000L * timeoutInSeconds;
            long finish = now + timeoutInMillis;
            while (isAlive(process) && (System.currentTimeMillis() < finish)) {
                Thread.sleep(10);
            }
            if (isAlive(process)) {
                throw new InterruptedException("Process timeout out after " + timeoutInSeconds + " seconds");
            }
            return process.exitValue();
        }
    }

    private static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }
}
