package io.sealights.plugins.sealightsjenkins.utils;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by Nadav on 5/15/2016.
 */

public class RenameFileCallable implements FilePath.FileCallable<Boolean> {
    private static final long serialVersionUID = 1L;

    private String oldName;
    private String newName;

    public RenameFileCallable(String newName, String oldName) {
        this.newName = newName;
        this.oldName = oldName;
    }


    @Override
    public Boolean invoke(File backupFile, VirtualChannel channel) throws IOException, InterruptedException {
        Path src = Paths.get(oldName);
        Path target = Paths.get(newName);
        try {
            Files.move(src, target, REPLACE_EXISTING);
        } catch (IOException e) {
            return false;
        }

        if (backupFile.exists())  {
            boolean delete = backupFile.delete();
            if (!delete)
            {
                return false;
            }
        }

        return true;
    }



    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }
}