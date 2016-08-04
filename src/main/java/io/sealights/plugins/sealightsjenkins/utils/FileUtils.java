package io.sealights.plugins.sealightsjenkins.utils;

import hudson.FilePath;
import hudson.model.Computer;
import hudson.remoting.VirtualChannel;
import hudson.slaves.SlaveComputer;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by Nadav on 4/26/2016.
 */
public class FileUtils {

    public static List<String> searchFilesByExtension(File directory, Boolean recursive, String extension) {

        List<String> returnedFiles = new ArrayList<String>();

        if (directory == null)
            throw new IllegalArgumentException("directory cannot be null");

        if (!directory.isDirectory()) {
            throw new RuntimeException("The specified directory is not a valid directory: " + directory);
        }
        if (!directory.canRead()) {
            throw new RuntimeException("There is no permission to read from the specified directory: " + directory);
        }
        File[] files = directory.listFiles();

        if (files != null) {
            for (File fileOrFolder : files) {
                if (fileOrFolder.isDirectory() && recursive) {
                    returnedFiles.addAll(searchFilesByExtension(fileOrFolder, recursive, extension));
                } else {
                    String name = fileOrFolder.getName();
                    String fileExtension = getFileExtension(name);
                    if (fileExtension.equalsIgnoreCase(extension)) {
                        returnedFiles.add(fileOrFolder.getAbsoluteFile().toString());
                    }
                }
            }
        }

        return returnedFiles;
    }
    public static void tryCopyFileFromLocalToSlave(Logger logger, String fileOnMaster, String fileOnSlave) throws IOException, InterruptedException {
        if (StringUtils.isNullOrEmpty(fileOnSlave)) {
            logger.warning("fileOnSlave is null. Skipping the copy.");
            return;
        }

        if (StringUtils.isNullOrEmpty(fileOnMaster)) {
            logger.warning("fileOnMaster is null. Skipping the copy.");
            return;
        }

        if (Computer.currentComputer() instanceof SlaveComputer) {
            VirtualChannel channel = Computer.currentComputer().getChannel();
            logger.debug("Current computer is: " + Computer.currentComputer().getName());
            logger.debug("Jenkins current computer is: " + Jenkins.MasterComputer.currentComputer().getName());

            FilePath fpOnRemote = new FilePath(channel, fileOnSlave);
            FilePath fpOnMaster = new FilePath(new File(fileOnMaster));
            logger.debug("fpOnMaster.getChannel(): " + fpOnMaster.getChannel());
            logger.debug("fpOnRemote: " + fpOnRemote.absolutize() + ", fpOnMaster:" + fpOnMaster.absolutize());
            fpOnMaster.copyTo(fpOnRemote);
        }
        else{
            logger.debug("There is no need to copy '" + fileOnSlave+ "' since the current machine is a master Jenkins machine.");
        }
    }

    public static void tryDeleteFile(Logger logger, String filename) throws IOException, InterruptedException {
        logger.info("Try deleting temp file: " + filename );
        FilePath fpOnMaster = new FilePath(new File(filename));
        if (fpOnMaster.exists()) {
            fpOnMaster.delete();
        }

        if (Computer.currentComputer() instanceof SlaveComputer) {
            VirtualChannel channel = Computer.currentComputer().getChannel();
            logger.debug("Current computer is: " + Computer.currentComputer().getName());
            logger.debug("Jenkins current computer is: " + Jenkins.MasterComputer.currentComputer().getName());
            FilePath filePathOnSlave = new FilePath(channel, filename);
            if (filePathOnSlave.exists()){
                filePathOnSlave.delete();
            }
        }

    }

    public static String getFileExtension(String file) {
        String extension = "";
        int lastDot = file.lastIndexOf('.');
        if (lastDot > 0) {
            extension = file.substring(lastDot + 1);
        }
        return extension;
    }
}
