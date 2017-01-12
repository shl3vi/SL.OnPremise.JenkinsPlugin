package io.sealights.plugins.sealightsjenkins.utils;

import java.io.File;

public class ArgumentFileResolver {

    public String resolve(Logger logger, String argument, String argumentFile){
        if (!StringUtils.isNullOrEmpty(argument)){
            return argument;
        }

        return tryGetValueFromFile(logger, argumentFile);
    }

    private static String tryGetValueFromFile(Logger logger, String argumentFile) {
        try {
            if (!StringUtils.isNullOrEmpty(argumentFile)) {
                if (new File(argumentFile).exists()) {
                    TextFileUtils textFileUtils = new TextFileUtils();
                    return textFileUtils.getContent(argumentFile);
                }
                logger.error("The provided file '" + argumentFile + "' does not exists.");
            }
        } catch (Exception e) {
            logger.error("Failed to get content of file '" + argumentFile + "'. Error: ", e);
        }
        return null;
    }

}
