package io.sealigths.plugins.sealightsjenkins.utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Nadav on 5/5/2016.
 */
public class Logger {
    private final String PREFIX = "[SeaLights] ";
    private PrintStream printStream;

    public Logger(PrintStream printStream)
    {
        this.printStream = printStream;
    }

    public void debug(String message)
    {
        log("DEBUG", message);
    }

    public void info(String message)
    {
        log("INFO", message);
    }

    public void warning(String message)
    {
        log("WARNING", message);
    }

    public void error(String message)
    {
        log("ERROR", message);
    }

    public void error(String message, Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        message += sw.toString();
        error(message);
    }

    private void log(String level, String message)
    {
        this.printStream.println(PREFIX + "[" + level + "]" + " " + message);
    }

}
