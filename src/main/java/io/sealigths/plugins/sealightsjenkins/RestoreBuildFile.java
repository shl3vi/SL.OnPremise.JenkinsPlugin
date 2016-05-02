package io.sealigths.plugins.sealightsjenkins;


import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.tasks.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import io.sealigths.plugins.sealightsjenkins.utils.FileAndFolderUtils;
import io.sealigths.plugins.sealightsjenkins.utils.FileUtils;
import io.sealigths.plugins.sealightsjenkins.utils.IncludeExcludeFilter;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nadav
 */
public class RestoreBuildFile extends Recorder {

    private boolean shouldRestore;
    private String patterns;
    private String folders;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public RestoreBuildFile(boolean shouldRestore, String folders,String patterns ) {
        this.shouldRestore = shouldRestore;
        this.folders = folders;
        this.patterns = patterns;
    }

    private void RestoreAllFilesInFolder(String rootFolder, PrintStream logger){
        log(logger, "searching in folder: " + rootFolder);
        boolean recursive = true;
        IncludeExcludeFilter filter = new IncludeExcludeFilter("*.slbak" , null);
        List<String> filesToRestore = FileAndFolderUtils.findAllFilesWithFilter(rootFolder, recursive, filter);
        for (String currentName : filesToRestore) {
            String newName = currentName.replace(".slbak","");
            boolean isSuccess = FileUtils.renameFileOrFolder(currentName, newName);
            if (isSuccess)
                log(logger, "Restored '" + currentName + "' to '" + newName + "'.");
            else
                log(logger, "Failed restoring '" + currentName + "' to '" + newName + "'.");
        }
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {

        PrintStream logger = listener.getLogger();

        if (this.shouldRestore) {
            log(logger, "Searching for files to restore...");
            FilePath ws = build.getWorkspace();
            if (ws == null) {
                log(logger, "Failed to retrieve workspace path.");
                return false;
            }

            List<String> folders = new ArrayList<>(Arrays.asList(this.folders.split("\\s*,\\s*")));
            folders.add(ws.getRemote());

            for (String folder : folders){
                RestoreAllFilesInFolder(folder, logger);
            }

        } else {
            log(logger, "No need to restore any files.");
        }

        return true;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public boolean isShouldRestore() {
        return shouldRestore;
    }

    public String getPatterns() {
        return patterns;
    }

    public String getFolders() {
        return folders;
    }

    public void setShouldRestore(boolean shouldRestore) {
        this.shouldRestore = shouldRestore;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
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

    private void log(PrintStream logger, String message) {
        message = "[SeaLights Jenkins Plugin] " + message;
        logger.println(message);
    }
}
