package io.sealigths.plugins.sealightsjenkins;

import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Nadav on 6/5/2016.
 */
public class DecideDefaultMavenCommand extends MasterToSlaveFileCallable<String> {
    private static final long serialVersionUID = -2327576423452215146L;
    // command line arguments.
    private final String arguments;

    public DecideDefaultMavenCommand(String arguments) {
        this.arguments = arguments;
    }

    public String invoke(File ws, VirtualChannel channel) throws IOException {
        String seed = null;

        // check for the -f option
        StringTokenizer tokens = new StringTokenizer(arguments);
        while (tokens.hasMoreTokens()) {
            String t = tokens.nextToken();
            if (t.equals("-f") && tokens.hasMoreTokens()) {
                File file = new File(ws, tokens.nextToken());
                if (!file.exists())
                    continue;   // looks like an error, but let the execution fail later
                seed = file.isDirectory() ?
                        /* in M1, you specify a directory in -f */ "maven"
                        /* in M2, you specify a POM file name.  */ : "mvn";
                break;
            }
        }

        if (seed == null) {
            // as of 1.212 (2008 April), I think Maven2 mostly replaced Maven1, so
            // switching to err on M2 side.
            seed = new File(ws, "project.xml").exists() ? "maven" : "mvn";
        }

        return seed;
    }
}
