package io.sealigths.plugins.sealightsjenkins.utils;

import hudson.Util;
import hudson.model.AbstractBuild;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by Nadav on 6/7/2016.
 */
public class JenkinsUtils {
    public String expandPathVariable(AbstractBuild<?, ?> build, String string)
    {
        String[] tokens = string.split(Pattern.quote(File.separator.toString()));
        for(int i=0; i< tokens.length; i++){
            tokens[i] = expandVariable(build, tokens[i]);
        }
        string = StringUtils.join(tokens, File.separatorChar);

        return string;
    }

    public String expandVariable(AbstractBuild<?, ?> build, String variable) {
        return Util.replaceMacro(variable, build.getBuildVariables());
    }

}
