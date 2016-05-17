package io.sealigths.plugins.sealightsjenkins;


import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.Computer;
import hudson.remoting.Channel;
import hudson.remoting.VirtualChannel;
import hudson.tasks.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import io.sealigths.plugins.sealightsjenkins.entities.FileBackupInfo;
import io.sealigths.plugins.sealightsjenkins.utils.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nadav
 */
public class RestoreBuildFile extends Recorder {

    private boolean shouldRestore;
    private String folders;
    private String parentPomFile;



    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public RestoreBuildFile(boolean shouldRestore, String folders, String parentPom) {
        this.shouldRestore = shouldRestore;
        this.folders = folders;
        this.parentPomFile = parentPom;
    }

    private void RestoreAllFilesInFolder(String rootFolder, Logger logger) throws IOException, InterruptedException {

        if (StringUtils.isNullOrEmpty(rootFolder)){
            return;
        }

        VirtualChannel channel = Computer.currentComputer().getChannel();
        FilePath rootFolderPath = new FilePath(channel, rootFolder);
        List<String> filesToRestore = rootFolderPath.act(new SearchFileCallable("**/*.slbak"));
        logger.info("searching in folder: '" + rootFolder +"', found '" + filesToRestore.size()+"' files.");
        for (String currentName : filesToRestore) {
            restoreSingleFile(currentName, logger);
        }
    }


    public String getParentPomFile() {
        return parentPomFile;
    }

    public void setParentPomFile(String parentPomFile) {
        this.parentPomFile = parentPomFile;
    }


    public void restoreSingleFile(String slbackFile, Logger logger) throws IOException, InterruptedException {
        String originalFile = slbackFile.replace(".slbak","");
        VirtualChannel channel = Computer.currentComputer().getChannel();
        FilePath backupFile = new FilePath(channel, slbackFile);
        if (!backupFile.exists())
            //File doesn't exist. Not need to restore.
            return;

        boolean isSuccess = backupFile.act(new RenameFileCallable(originalFile, slbackFile));
        if (isSuccess)
            logger.info("Restored '" + slbackFile + "' to '" + originalFile + "'.");
        else
            logger.error("Failed restoring '" + slbackFile + "' to '" + originalFile + "'.");
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        Logger logger = new Logger(listener.getLogger());

        if (this.shouldRestore) {
            logger.info("Searching for files to restore...");
            FilePath ws = build.getWorkspace();
            if (ws == null) {
                logger.error("Failed to retrieve workspace path.");
                return false;
            }

            List<String> folders = new ArrayList<>(Arrays.asList(this.folders.split("\\s*,\\s*")));
            folders.add(ws.getRemote());

            for (String folder : folders){
                RestoreAllFilesInFolder(folder, logger);
            }

            if (this.parentPomFile != null)
                restoreSingleFile(this.parentPomFile + ".slbak", logger);


        } else {
            logger.info("No need to restore any files.");
        }

        return true;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     * @return boolean
     */
    public boolean isShouldRestore() {
        return shouldRestore;
    }


    public String getFolders() {
        return folders;
    }

    public void setShouldRestore(boolean shouldRestore) {
        this.shouldRestore = shouldRestore;
    }


    public void setFolders(String folders) {
        this.folders = folders;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }



    /**
     * Descriptor for {@link RestoreBuildFile}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p>
     * See <tt>src/main/resources/org/jenkinsci/plugins/testExample/RestoreBuildFile/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */


        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "SeaLights - Restore Build File";
        }

    }

}
