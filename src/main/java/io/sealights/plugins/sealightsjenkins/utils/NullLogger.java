package io.sealights.plugins.sealightsjenkins.utils;

/**
 * Created by shahar on 11/12/2016.
 */
public class NullLogger extends Logger {

    public NullLogger() {
        super(null);
    }

    @Override
    public void debug(String message){}

    @Override
    public void info(String message){}

    @Override
    public void warning(String message){}

    @Override
    public void error(String message){}

    @Override
    public void error(String message, Throwable throwable){}

}
