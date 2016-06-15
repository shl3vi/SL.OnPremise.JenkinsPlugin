package io.sealigths.plugins.sealightsjenkins.exceptions;

import java.util.IllegalFormatCodePointException;

/**
 * Created by shahar on 6/9/2016.
 */
public class SeaLightsIllegalStateException extends IllegalStateException{

    public SeaLightsIllegalStateException(String msg){
        super(msg);
    }
}
