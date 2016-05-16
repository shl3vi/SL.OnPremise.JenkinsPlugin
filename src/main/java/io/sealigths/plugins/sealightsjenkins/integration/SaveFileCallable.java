package io.sealigths.plugins.sealightsjenkins.integration;


import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Nadav on 5/15/2016.
 */

public class SaveFileCallable implements FilePath.FileCallable<String> {
    private static final long serialVersionUID = 1L;


    private String data;

    public SaveFileCallable(String data) {
        this.data = data;
    }


    @Override
    public String invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {

        String status = "";
        try{
            if (!file.exists()){
                status += " doesn't exists.";
                boolean newFile = file.createNewFile();
                status += "New file: " + newFile;

            }

            status += "before write to 'file'. Data.length:" + data.length();
            FilePath fp  = new FilePath(file);
            fp.write(data, "UTF-8");

            status += "All good. File:" + file.getAbsolutePath();
            return status;
        }catch(Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            status += ". Error!!! Exception: " + sw.toString();
        }
        return status;
    }


    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }
}